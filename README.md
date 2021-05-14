# HelloJni - Java and C++ say hello!
HelloJni is a simple example of how you can call native code written in C++ from a Java application using the Java Native Interface (**JNI**).

## Why?
There are times when Java alone may not be enough to get the job done. For example:
* You want to use an existing C/C++ library in your Java application (instead of rewriting it in Java).
* You want to use OS-dependent or HW-dependent features which are not supported by the Java Virtual Machine (for example, device drivers or kernel calls).
* You want to implement the business logic of your mobile app in C/C++, and use it both on iOS and Android

With Java you can implement these use cases with the Java Native Interface (JNI), but there are no tutorials in the [official JNI documentation](https://docs.oracle.com/en/java/javase/16/docs/specs/jni/index.html).
The goal of this repo is to fill this gap, by providing a gentle introduction to JNI.

## How to build and run
### Prerequisites
Before you begin, ensure you have met the following requirements:
* You installed a Java Development Kit (JDK) supporting **Java 10+**.
* You can call `javac` and `java` from the command line
* You installed GCC (or [Mingw-w64](http://mingw-w64.org/) on Windows)
* You can call `g++` from the command line

### Steps
1. Clone the repo

  ```
  git clone https://github.com/ipmartella/hellojni hellojni
  cd hellojni
  ```

2. Compile the Java source code (`HelloJni.java`), and generate the native header file `HelloJni.h` in the current folder (`-h .`)
  ```
  javac HelloJni.java
  javac HelloJni.java -h .
  ```

3. Compile the C++ source code (`HelloJni.cc`) and generate the native shared library `libhellojni_cpp.so` (`hellojni_cpp.dll` on Windows)
  If you are on Linux:
  ```
    export JDK_HOME=/usr/lib/jvm/java-16-openjdk-amd64 <-- replace this with the path to your JDK
    
    g++ -shared -fPIC -I "$JDK_HOME/include" -I "$JDK_HOME/include/linux" HelloJni.cc -o libhellojni_cpp.so
  ```

  If you are on Windows
  ```
    set JDK_HOME=C:\Program Files\Java\jdk-16.0.1 <-- replace this with the path to your JDK

    g++ -shared -fPIC -I "%JDK_HOME%/include" -I "%JDK_HOME%/include/win32" HelloJni.cc -o hellojni_cpp.dll
  ```

4. Run the HelloJni application
  ```
    java HelloJni
  ```


## How does it work?
The key points for making Java and C++ work together are:
- The *signature* of the C++ method is in the Java source code.
- The *implementation* of the C++ method is in the native shared library.
- The Java code loads the native shared library, and then calls the C++ method.

Let's see how to develop both the Java source code and C++ source code.

### HelloJni.java
HelloJni.java does three things:
1. It loads the `hellojni_cpp` native shared library, which contains our C++ implementation:
```java
static {
    System.load(new File(System.mapLibraryName("hellojni_cpp")).getAbsolutePath());
}
```
2. It declares the **native** Java method `sayHelloFromCpp()`, so that the JVM knows what to search for into the `hellojni_cpp` native shared library:
```java
private native void sayHelloFromCpp();
```

3. It calls the `sayHelloFromCpp()` native Java method:
```java
public static void main(String[] argv) {
    System.out.println("Hi, I am Java. Nice to meet you!");

    HelloJni app = new HelloJni();
    app.sayHelloFromCpp();
}
```
That's it! 

### HelloJni.cc
HelloJni.cc does only one thing: it implements the `HelloJni.sayHelloFromCpp()` method.
```c++
#include "HelloJni.h"

JNIEXPORT void JNICALL Java_HelloJni_sayHelloFromCpp (JNIEnv *, jobject) {
    std::cout << "Hello I'm C++, nice to meet you too!" << std::endl;
}
```
The method definition (actually *function* definition) may seem scary, but we can easily recognize "**Java** _ **HelloJni**(the name of the class) _ **sayHelloFromCpp** (the name of the method)" in the middle of the boilerplate.

The good news is that the Java compiler (`javac`) can generate this signature for us!
When we call `javac HelloJni.java -h <directory>`, `javac` compiles `HelloJni.java` into `HelloJni.class` (the Java bytecode) and - if the Java source code contains *native* method declarations - it generates the `HelloJni.h` file in `<directory>` 

The `HelloJni.h` file contains the signature of the function the JVM will look for in the shared native library when calling the `HelloJni.sayHelloFromCpp()` method. Once the signature is known, we only need to to implement the desired behavior (in this case, just printing a message to console).

### Compiling the native shared library
Compiling a native shared library for Java is no different than compiling other shared libraries. A few additional remarks:
* You must use the `-fPIC` option (search for "*Position independent code*" for more info)
* You must tell your compiler where to find the general JNI headers (`-I $JDK_HOME/include/` option), and the platform specific JNI headers (`-I $JDK_HOME/include/[linux|windows]` option).

The JNI headers are installed together with your Java Development Kit (JDK), so make sure to set the `JDK_HOME` environment variable accordingly.


## Where to go from here?
So now you know the basics of how to run C++ from Java!
Of course, we barely scratched the surface here, but thanks to this foundation you can explore additional topics such as passing arguments, returning values, dealing with objects.

If you want to know more, stay tuned for further examples or open a Github issue :)

## License
This project uses the following license: [Unlicense](https://unlicense.org)

