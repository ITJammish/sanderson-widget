# Keeping up with Sanderson

[Brandon Sanderson](https://en.wikipedia.org/wiki/Brandon_Sanderson), sci-fi and fantasy author, is known in the industry for writing _incredibly_ fast. During the pandemic Brandon was able to write [5 extra novels](https://www.youtube.com/watch?v=6a-k6eaT-jQ) in the time freed up by cancelled book touring. The man is a machine, and this is good news for his readers! He has so many projects and book series that it's hard to know when the next book is coming out. Thankfully Brandon's team let us know by publishing his writing progress on his [website]([https://www.brandonsanderson.com/](https://www.brandonsanderson.com/)), but wouldn't it be great if you could be more seamlessly kept up to date? Yes, there's a mailing list... but now there's an Android home screen widget too!

The **Sanderson progress widget** is an indie project that I wanted to build for myself, but made sense to publish for the wider Sanderfans.

## "What is a Widget?"
A home screen widget is an extension of an application that you can see outside of that app. It's like a window to a small part of an app's functionality that persists in an area of your home screen. Many apps have widgets that can be super helpful, but they're not widely promoted.

Some widgets are stand alone and don't have a core app that can be run, they just kind of exist as widgets and that's all they do. The Sanderson progress widget is one such widget.

## "How does it work?"
When you place the widget a web call is made to https://www.brandonsanderson.com/ just like if you were visiting the page yourself in a browser, but rather than render the page we scan through the raw html and pull out ([scrape](https://en.wikipedia.org/wiki/Web_scraping)) the information that we're interested in and pass that information to the widget to display it on your home screen. As long as the widget is on your home screen we'll poll for changes every so often in case the writing progress on the website has been updated.

## "No, I mean; *how does it work*? How can I add a widget to my home screen?"
The first step is to get the widget app (it's an app as far as your device is concerned) onto your device. It is not currently available on the Google PlayStore, but should be soon. Until then, any developer can pull this project, compile and install it freely.

Once installed you have to find the Widget menu (Widgets aren't allowed to play with all the other apps, they get their own menu). Different phones have different ways of getting to this menu. Even Google doesn't make it obvious, but thankfully they've got some information [here](https://support.google.com/android/answer/9450271?hl=en-GB#zippy=%2Cadd-or-resize-a-widget) that can help.

## Getting started
In order to run this project simply pull the code, build, install and have fun. Since we're not using an API there is no user ID keys required, but please be respectful and don't [DOS](https://en.wikipedia.org/wiki/Denial-of-service_attack) Brandon's website. Or if that's really your bag, please don't use this project to do so.

If you'd like to learn more about coding widgets [this is a good introduction](https://developer.android.com/guide/topics/appwidgets/overview). [Android Studio](https://developer.android.com/studio) also offers a Widget template that is a very useful place to start. I also found [this video tutorial series](https://www.youtube.com/playlist?list=PLrnPJCHvNZuDCoET8jL2VK4YVRNhVEy0K) by [Coding in Flow](https://www.youtube.com/c/CodinginFlow) very helpful.

Thank you!