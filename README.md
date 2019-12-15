# PinUIAndroid
[![](https://jitpack.io/v/robinkrsingh/PinUiAndroid.svg)](https://jitpack.io/#robinkrsingh/PinUiAndroid)

Library for pin entry input field. The length of pin can be set dynamically. Also It have backspace delete support for input.


Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.robinkrsingh:PinUiAndroid:0.1.0'
	}
  
  Example:
  
    
      <com.robin.pinui.PinEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text=""
        app:length="5"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" 
        />
        
