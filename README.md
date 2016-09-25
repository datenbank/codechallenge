# codechallenge

The service is deployed here:

[https://822qlg4tye.execute-api.us-west-2.amazonaws.com/prod/TrustPilotScore?domain=trustpilot.com](https://822qlg4tye.execute-api.us-west-2.amazonaws.com/prod/TrustPilotScore?domain=trustpilot.com)


call it with the domain paramter.

    ?domain=trustpilot.com

The Service will get the latest 300 reviews, and make a weigthed average where most recent will affect the score more than older reviews. Reviews decline in importance as time goes and after 365 days the review will not affect the score anymore.

Results returned is with HTTP Response code 200

    {
    "TrustScore": 3.8
    }
    
But if an error accour the following is returned with a HTTP Response code 500

    {"errorMessage":"Domain parameter not set!","errorType":"java.lang.Exception",
    "stackTrace":["sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)"
    ,"sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)"
    ,"sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)"
    ,"java.lang.reflect.Constructor.newInstance(Constructor.java:423)"
    ,"org.codehaus.groovy.reflection.CachedConstructor.invoke(CachedConstructor.java:83)"
    ,"org.codehaus.groovy.runtime.callsite.ConstructorSite$ConstructorSiteNoUnwrapNoCoerce.callConstructor(ConstructorSite.java:105)"
    ,"org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:247)"
    ,"codechallenge.TrustScore.handler(TrustScore.groovy:137)"
    ,"sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)"
    ,"sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)"
    ,"sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)"
    ,"java.lang.reflect.Method.invoke(Method.java:498)"]}
