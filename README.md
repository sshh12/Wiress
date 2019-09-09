# Wiress

> Unlimited call minutes are common place, unlimited data not so much. That's the advantage of Wiress, dialup internet for mobile.

![spectrogram](https://user-images.githubusercontent.com/6625384/64572938-f68e7b80-d32e-11e9-94c9-a4c973b0c0bd.png) 

## What 

Wiress allows users to use the internet through a phone call (kind of like dial up or reversed VoIP).

```
[Phone] <---callaudio---> [Twilio] <---websockets/hooks---> [Wiress Servers] <---http---> [Internet]
```

## Devs

* [@sshh12](https://github.com/sshh12)
* [@Legoben](https://github.com/Legoben)

## Status

What does work:
* Data to audio generation
* Audio to data decoding from both a pure audio `.wav` and noisy microphone audio (but not through-phone-call audio)
* A backend which supports multiple phone calls and live recording and playback (not traditional possible with Twilio but was accomplished with some work arounds)

Unfortunatly Wiress was unable to be complete (at least for the near feature) because of two fundamental issues:

#### 1. Android’s phone-call firmware 

> Android doesn't let us feed audio into a call or stream audio from a call. This crazy guy on Stack Overflow is like a yeah it's possible to insert the call audio but you have to root your phone and then basically rewrite all of the call handling logic in the Android Kernel so it goes through your app instead of just the microphone. But this isn't the end of the world, we can tape two phones and together and have one responsible for calling and feed it into the mic/speaker of the other one.

#### 2. Built in call compression and optimization

> To support more and clearer voice calls, both phones and telephone companies use algorithms which modify the original audio significantly. Tests showed that frequencies outside of normal vocal range and pure (non-vocal, e.g. sin) tones would be nearly completely filtered out. Even when using vocal frequencies the audio would sometimes cut out in a non-deterministic way.