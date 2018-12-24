## 单例模式
* 概念：程序某个对象只可能存在一个(在一个JVM里)，那我们称这个类为单例类，单例对象的类只能允许一个实例存在
* 作用：减少内存紧张问题，并且管理简单
* 分类：饿汉模式和懒汉模式
* 区别：
    * 前者在类初始化就在堆内存就创建，后者在调用getInstance()方法时初始化
    * 详细讲解：
        * 由于前者在声明时用static修饰，所以我们可以直接通过ClassName.singleton拿到对象的引用
        * 而后者不用声明static修饰，同时默认赋值为NULL，所以必须通过getInstance()方法拿到对象
        * 二者都可以通过反射破坏单例模式
        * 饿汉模式为线程安全，懒汉模式线程不安全
* 单例模式-饿汉模式demo 常量和静态代码块
   ```java
   public class Singleton{
      private final static Singleton singleton = new Singleton();
      private Singleton(){}
      public static Singleton getInstance(){
        return singleton;
      }  
    }
    ```
    优点：这种写法比较简单，就是在类装载的时候就完成实例化。避免了线程同步问题。
    
    缺点：在类装载的时候就完成实例化，没有达到Lazy Loading的效果。如果从始至终从未使用过这个实例，则会造成内存的浪费。
* 单例模式-懒汉模式（错误写法）这种写法不是现场安全的
    ```java
    public class ErrorSingleton{
      private ErrorSingleton errorSingleton = null;
      private ErrorSingleton(){}
      public ErrorSingleton getErrorSingleton(){
          if(errorSingleton == null){
              errorSingleton = new ErrorSingleton();  
          }
          return errorSingleton;
      }
    }
    ```
* 通过反射破坏单例规则
    1. 通过Class拿到构造器对象
    2. 设置setAccessible(true)
    3. 通过构造器对象生成一个实例
* 多线程单例
    1. 使用synchronized关键字锁住getInstacne()方法，这种方案锁粒度太粗，影响性能
    2. 使用synchronized锁住new方法，双重校验，并且引用要有volatile修饰，防止内存重排序
    3. demo
    ```java
    public class Singleton{
      private static volatile Singleton singleton;
      private Singleton(){}
      public static Singleton getInstance(){
          if(singleton == null){
              synchronized (Singleton.class){
                  if(singleton == null){
                      singleton = new Singleton(); 
                  }  
              } 
          }  
        return singleton;
      }
    }
    ```
*枚举不能被反射：我们看下Constructor类的newInstance方法源码：
```java
public final class Constructor<T> extends Executable {
    
 public T newInstance(Object ... initargs)
         throws InstantiationException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException
     {
         if (!override) {
             if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
                 Class<?> caller = Reflection.getCallerClass();
                 checkAccess(caller, clazz, null, modifiers);
             }
         }
         if ((clazz.getModifiers() & Modifier.ENUM) != 0){
             throw new IllegalArgumentException("Cannot reflectively create enum objects");
         }
         ConstructorAccessor ca = constructorAccessor;   // read volatile
         if (ca == null) {
             ca = acquireConstructorAccessor();
         }
         @SuppressWarnings("unchecked")
         T inst = (T) ca.newInstance(initargs);
         return inst;
     }   
}
```


```java
public final class Constructor<T> extends Executable {
    
 public T newInstance(Object ... initargs)
         throws InstantiationException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException
     {
         if (!override) {
             if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
                 Class<?> caller = Reflection.getCallerClass();
                 checkAccess(caller, clazz, null, modifiers);
             }
         }
         if ((clazz.getModifiers() & Modifier.ENUM) != 0){
             throw new IllegalArgumentException("Cannot reflectively create enum objects");
         }
         ConstructorAccessor ca = constructorAccessor;   // read volatile
         if (ca == null) {
             ca = acquireConstructorAccessor();
         }
         @SuppressWarnings("unchecked")
         T inst = (T) ca.newInstance(initargs);
         return inst;
     }   
}
```

枚举在进行反序列化的时候：
```java
public class ObjectOutputStream
    extends OutputStream implements ObjectOutput, ObjectStreamConstants{
    private Enum<?> readEnum(boolean unshared) throws IOException {
            if (bin.readByte() != TC_ENUM) {
                throw new InternalError();
            }
    
            ObjectStreamClass desc = readClassDesc(false);
            if (!desc.isEnum()) {
                throw new InvalidClassException("non-enum class: " + desc);
            }
    
            int enumHandle = handles.assign(unshared ? unsharedMarker : null);
            ClassNotFoundException resolveEx = desc.getResolveException();
            if (resolveEx != null) {
                handles.markException(enumHandle, resolveEx);
            }
    
            String name = readString(false);
            Enum<?> result = null;
            Class<?> cl = desc.forClass();
            if (cl != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Enum<?> en = Enum.valueOf((Class)cl, name);
                    result = en;
                } catch (IllegalArgumentException ex) {
                    throw (IOException) new InvalidObjectException(
                        "enum constant " + name + " does not exist in " +
                        cl).initCause(ex);
                }
                if (!unshared) {
                    handles.setObject(enumHandle, result);
                }
            }
    
            handles.finish(enumHandle);
            passHandle = enumHandle;
            return result;
        }
}
```

* 总结
   -
   * 设计单例最佳为ENUM类
   
## 参考：
(https://www.cnblogs.com/zhaoyan001/p/6365064.html)
(https://www.cnblogs.com/chiclee/p/9097772.html)
(https://www.cnblogs.com/chiclee/p/9097772.html)
