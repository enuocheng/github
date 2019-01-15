## 事件、监听器

Java事件机制包括三个部分：事件、事件监听器、事件源。其中事件类中包含事件源的实例，来标识事件的发出者；事件监听器类则包含了事件被触发时的响应函数，
业务逻辑写在该响应函数中；而事件源则有一个事件监听器列表，当事件触发时，通知所有的监听者，采用的是观察者模式 (发布-订阅模式)。

### 事件类。一般继承自java.util.EventObject类，封装了事件源对象及跟事件相关的信息。

```java

import java.util.EventObject;
 
/** 
 * @Title: ValueChangeEvent.java 
 * @Package  
 * @Description:  
 * @author Mr.Simple bboyfeiyu@gmail.com 
 * @date Apr 5, 2013 2:10:39 PM 
 * @version V1.0 
 */
 
 
/**
 * 事件类,包含了事件源
 * @ClassName: ValueChangeEvent 
 * @Description: 
 * @author Mr.Simple 
 * @date Apr 5, 2013 2:12:37 PM 
 *
 */
public class ButtonClickEvent extends EventObject {
 
	/**
	 * 字段：
	 */
	private static final long serialVersionUID = 1L;
	// 事件源
	private Object mSourceObject = null;
	private String mTag = "";
	
	/**
	 * 构造函数
	 * @param sObject
	 */
	public ButtonClickEvent(Object sObject){
		super(sObject);
		mSourceObject = sObject;
	}
	
	/**
	 * 构造函数
	 * @param sObject
	 * @param tag
	 */
	public ButtonClickEvent(Object sObject, String  tag){
		super(sObject);
		mSourceObject = sObject;
		mTag = tag;
	}
	
	/**
	 * 获取事件源
	 * (non-Javadoc)
	 * @see java.util.EventObject#getSource()
	 */
	public Object getSource() {
		return mSourceObject;
	}
	
	/**
	 * 
	 * @Title: setSource 
	 * @Description: 设置事件源
	 * @param obj    
	 * @return void    
	 * @throws
	 */
	public void setSource(Object obj) {
		mSourceObject = obj;
	}
	
	/**
	 * 
	 * @Title: getTag 
	 * @Description: 获得tag
	 * @return    
	 * @return String    
	 * @throws
	 */
	public String getTag(){
		return mTag;
	}
	
	/**
	 * 
	 * @Title: setTag 
	 * @Description: 设置tag
	 * @param tag    
	 * @return void    
	 * @throws
	 */
	public void setTag(String tag) {
		mTag = tag;
	}
 
}

```

### 事件监听器。实现java.util.EventListener接口,注册在事件源上,当事件源触发事件时,取得相应的监听器调用其内部的回调方法。

```java
private static class ButtonClickListenerInner implements EventListener{
		/**
		 * 
		 * @Title: ItemClicked 
		 * @Description: 点击事件
		 * @param event    
		 * @return void    
		 * @throws
		 */
		public void ButtonClicked(ButtonClickEvent event ) {
			// 获取事件源
			ButtonDemo source = (ButtonDemo)event.getSource();
			System.out.println("内部静态监听类@_@ 你点击的是 : " + source.getItemString()) ;
		}
	}
```

### 事件源。事件触发的地方，由于事件源的某项属性或状态发生了改变(比如Button被单击等)导致某项事件发生。换句话说就是生成了相应的事件对象。
因为事件监听器要注册在事件源上,所以事件源类中应该含有用来存储事件监听器的容器(List,Set等等)。

```java
public class ButtonDemo {
	// item文本文字
	private String mItemName = "";
	// 监听器哈希集合,可以注册多个监听器
	private Set<EventListener> mClickListeners = null ;
	
	/**
	 * 构造函数
	 *
	 */
	public ButtonDemo(){
		//  监听器列表
		mClickListeners = new HashSet<EventListener>();
		mItemName = "Defualt Item Name";
	}
	
	/**
	 * 构造函数
	 * @param itemString
	 */
	public ButtonDemo(String itemString){
		mItemName = itemString;
		mClickListeners = new HashSet<EventListener>();
	}
	
	/**
	 * 
	 * @Title: AddItemClickListener 
	 * @Description: 添加监听器
	 * @param listener    
	 * @return void    
	 * @throws
	 */
	public void AddItemClickListener(EventListener listener){
		// 添加到监听器列表
		this.mClickListeners.add(listener) ;
	}
	
	/**
	 * 
	 * @Title: ItemClick 
	 * @Description: 模拟点击事件,触发事件则通知所有监听器    
	 * @return void    
	 * @throws
	 */
	public void ButtonClick() {
		// 通知所有监听者
		Notifies() ;
	}
	
	/**
	 * 
	 * @Title: Notifies 
	 * @Description: 通知所有监听者   
	 * @return void    
	 * @throws
	 */
	private void Notifies() {
		Iterator<EventListener> iterator = mClickListeners.iterator();
		while (iterator.hasNext()) {
			// 获取当前的对象
			ButtonClickListenerInner listener = (ButtonClickListenerInner) iterator.next();
			// 事件触发,事件的构造函数参数为事件源
			listener.ButtonClicked(new ButtonClickEvent(this));
			
		}
	}
	
	/**
	 * 
	 * @Title: getItemString 
	 * @Description: 返回该项的名字 
	 * @return String    
	 * @throws
	 */
	public String getItemString() {
		return mItemName;
	}
	
	
	/**
	 * 
	 * @Title: main 
	 * @Description: main方法
	 * @param args    
	 * @return void    
	 * @throws
	 */
	public static void main(String[] args) {
		ButtonDemo buttonDemo = new ButtonDemo("Hello, I am a ButtonDemo") ;
		// 添加监听器
		buttonDemo.AddItemClickListener(new ButtonClickListenerInner()) ;
		// 事件触发
		buttonDemo.ButtonClick();

    }
}
```


## 参考：
(https://blog.csdn.net/bboyfeiyu/article/details/8761345)
