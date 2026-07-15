plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("mylibConvention") {
            id = "mylib.convention"
            implementationClass = "MyLibConventionPlugin"
        }
    }
}
