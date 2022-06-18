package com.example.myapp

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}