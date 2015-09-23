/**
 * Created with IntelliJ IDEA.
 * User: Andrey Assaul
 * Date: 14.08.2015
 * Time: 17:06
 */
package com.trembit.localContentLoader {
	import flash.display.Loader;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;
	import flash.net.URLRequest;
	import flash.net.URLVariables;
	import flash.system.LoaderContext;
	import flash.utils.ByteArray;

	public class LocalContentLoader extends Loader{

		private static var extContext:ExtensionContext;

		private static function get extensionContext():ExtensionContext{
			if(!extContext){
				extContext = ExtensionContext.createExtensionContext("com.trembit.extension.LocalContentLoader", null);
			}
			return extContext;
		}

		private var uuid:String;
		private var currentLoaderContext:LoaderContext;

		override public function load(request:URLRequest, context:LoaderContext = null):void {
			if(uuid){
				extensionContext.call(ContextMethod.STOP_LOADER_BY_UUID, uuid);
				uuid = null;
			}
			var uri:String = request.url;
			if(uri.indexOf("content://") == 0){
				var urlVars:URLVariables = request.data as URLVariables;
				if(urlVars){
					var urlVarsString:String = urlVars.toString();
					if(urlVarsString && urlVarsString != ""){
						uri+=("?"+urlVarsString);
					}
				}
				extensionContext.addEventListener(StatusEvent.STATUS, onContextStatus);
				var checkResult:String = String(extensionContext.call(ContextMethod.LOAD_CONTENT, uri));
				if(checkResult != null){
					if(checkResult.indexOf("://") != -1){
						var newRequest:URLRequest = new URLRequest(checkResult);
						extensionContext.removeEventListener(StatusEvent.STATUS, onContextStatus);
						super.load(newRequest, context);
					}else{
						uuid = checkResult;
						currentLoaderContext = context;
					}
				}else{
					extensionContext.removeEventListener(StatusEvent.STATUS, onContextStatus);
					super.load(request, context);
				}
			}else{
				extensionContext.removeEventListener(StatusEvent.STATUS, onContextStatus);
				super.load(request, context);
			}
		}

		override public function close():void {
			super.close();
			if(uuid){
				extensionContext.call(ContextMethod.STOP_LOADER_BY_UUID, uuid);
				uuid = null;
			}
		}

		private function onContextStatus(event:StatusEvent):void {
			var uuid:String = event.level;
			if(this.uuid != uuid){
				if(this.uuid == null){
					extensionContext.removeEventListener(StatusEvent.STATUS, onContextStatus);
				}
				return;
			}
			extensionContext.removeEventListener(StatusEvent.STATUS, onContextStatus);
			switch(event.code){
				case ContextStatus.LOAD_ERROR:
					uuid = null;
					currentLoaderContext = null;
					trace("Load content error");
					break;
				case ContextStatus.LOAD_FINISHED:
					processBytesLoaded();
					break;
			}
		}

		private function processBytesLoaded():void{
			var bytes:ByteArray = ByteArray(extensionContext.call(ContextMethod.GET_CONTENT_BYTES_BY_UUID, uuid));
			loadBytes(bytes, currentLoaderContext);
			uuid = null;
			currentLoaderContext = null;
		}
	}
}