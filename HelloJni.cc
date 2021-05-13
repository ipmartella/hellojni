#include <iostream>
#include "HelloJni.h"

JNIEXPORT void JNICALL Java_HelloJni_sayHelloFromCpp (JNIEnv *, jobject) {
    std::cout << "Hello I'm C++, nice to meet you too!" << std::endl;
}
