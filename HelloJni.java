import java.io.File;

public class HelloJni {

    static {
        System.load(new File(System.mapLibraryName("hellojni_cpp")).getAbsolutePath());
    }

    private native void sayHelloFromCpp();

    public static void main(String[] argv) {
        System.out.println("Hi, this is Java nice to meet you!");

        HelloJni app = new HelloJni();
        app.sayHelloFromCpp();
    }

}
