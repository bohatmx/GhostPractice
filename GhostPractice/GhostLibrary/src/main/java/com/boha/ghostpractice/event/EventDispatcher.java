package com.boha.ghostpractice.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
 
public interface EventDispatcher {
 
	/**
	 * Register an event listener
	 * @param type		String the name of the event to listen to
	 * @param listener	EventListener the listener
	 */
	public void addEventListener(String type, EventListener listener);
	/**
	 * Unregister an event listener
	 * @param type		String the name of the event registered to
	 * @param listener	EventListener the listener
	 */
	public void removeEventlistener(String type, EventListener listener);
	/**
	 * Remomve all listeners
	 */
	public void removeAllListeners();
	/**
	 * Dispatch an Event
	 * @param event	Event
	 */
	public void dispatchEvent(Event event) ;
	/**
	 * Query to see if there are any listeners registered to a certain event name
	 * @param type	String the event name to query
	 * @return	boolean
	 */
	public boolean willTrigger(String type);
 
 
	public class EventDispatcherImpl implements EventDispatcher {
		protected HashMap<String , ArrayList<EventListener>> _listenersMap;
		protected EventDispatcher _eventTarget;
 
		/**
		 * Constructor - intended for inheritance
		 */
		public EventDispatcherImpl() {
			removeAllListeners();
		}
 
		/**
		 * Constructor - intended for composition
		 * @param eventTarget EventListener to use as the event target in the Event sent
		 */
		public EventDispatcherImpl(EventDispatcher eventTarget) {
			_eventTarget = eventTarget;
		}
 
		public void addEventListener(String type, EventListener listener) {
			removeEventlistener(type, listener);
			ArrayList<EventListener> listeners = _listenersMap.get(type);
			if ( listeners == null ) {
				listeners = new ArrayList<EventListener>();
				_listenersMap.put(type, listeners);
			}
			listeners.add(listener);
		}
 
		public void removeEventlistener(String type, EventListener listener) {
			ArrayList<EventListener> listeners = _listenersMap.get(type);
			if ( listeners != null ) {
				Iterator<EventListener> items = listeners.iterator();
		        while( items.hasNext() ) {
		            if ( items.next() == listener ) {
		            	items.remove();
		            	return;
		            }
		        }
		        if ( listeners.isEmpty() ) {
		        	_listenersMap.remove(type);
		        }
			}
		}
 
		public void removeAllListeners() {
			_listenersMap = new HashMap<String , ArrayList<EventListener>>();
		}
 
		public void dispatchEvent(Event event) {
			ArrayList<EventListener> listeners = _listenersMap.get(event.type);
			if ( listeners != null ) {
				event.target = _eventTarget != null ? _eventTarget : this;
				Iterator<EventListener> items = listeners.iterator();
		        while( items.hasNext() ) {
		            items.next().trapEvent(event);
		        }
			}
		}
 
		public boolean willTrigger(String type) {
			ArrayList<EventListener> listeners = _listenersMap.get(type);
			return listeners == null || listeners.isEmpty();
		}
	}
}