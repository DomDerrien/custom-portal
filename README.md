<div style="text-align:center;font-size:18px;">
Custom Portal - v 1.0
</div>

Origin
======
When Google launched the iGoogle service in 2005, it became my default home page. I especially
liked the capability its dashboard capabilities. My favorite gadget was the one displaying my
[bookmarks](https://www.google.com/bookmarks/).

When [they announced its retirement](https://support.google.com/websearch/answer/2664197?hl=en),
I looked for a viable alternative, a hosted service, with multiple account support, with an
adaptative layout, etc.

Because I did not find something for my taste, because I see here an opportunity to mix many of 
my favorite technologies, I started this project to primarily store my bookmarks.. 
 
Key components
==============
 
Google App Engine for the identity management, the persistence layer, and the Web server
----------------------------------------------------------------------------------------
 
I've been using the [Google App Engine](https://developers.google.com/appengine/) service since the beginning, for personal and professional
services. So I naturally decided to use GAE to host this service.
 
Objectify for the ORM
---------------------
 
After one project with JDO on GAE, and others with JPA and Hibernate (JPA 2 compliant), I switched to [Objectify](http://code.google.com/p/objectify-appengine/).
While being specifically made for Google Datastore (with a smaller set of features, like the absence of join
support), it offers a standardized approach (in the JDO spirit) and it's an effecient implementation.
 
RestEasy for the RESTful API
----------------------------
 
A long time ago, I designed and implemented my own transport layer between back-ends and Web clients.
Nowadays, there are so many libraries doing it with the support of huge communities, like Spring or Jersey.
For this project, I've chosen the light [RestEasy](http://www.jboss.org/resteasy).
 
Jackson for the transport protocol
----------------------------------
 
[Jackson](http://jackson.codehaus.org/) is the best library for manipulating the JSON data format within the JVM.

Dojo Tookit for the JavaScript mastering
----------------------------------------

As a contributor of a Web 2.0 framework for the Oracle Collaboration Suite in early 2000, I really like the JavaScript
programming language while recognizing it's a difficult one to master. When I joined IBM Rational, I had the chance to 
use and to contribute to (through the IBM community) the [Dojo toolkit](http://dojotoolkit.org). Since then, it's my
favorite JavaScript framework and with the [AMD](http://requirejs.org/docs/whyamd.html#amd) support, its [build
system](https://dojotoolkit.org/reference-guide/1.9/build/buildSystem.html#build-buildsystem), and its large [widget
library](https://dojotoolkit.org/reference-guide/1.9/dijit/index.html), it's the perfect fit for enterprise-class Web applications.

Bootstrap for the presentation
------------------------------

As many programmers, my UI design skills are limited. Relying on [Bootstrap](http://getbootstrap.com/) which provides nice looking
widgets and a grid management system is an easy win. As Martin Pengelly-Philips did port the Bootstrap theme to Dojo, I use his
[dbootstrap](https://github.com/thesociable/dbootstrap) library.

And friends
----------------------------------

* [Guice](https://code.google.com/p/google-guice/wiki/Motivation) for the dependency injection
* [Apache Shiro](http://shiro.apache.org/guice.html) for the authorization
* [Joda Time](http://joda-time.sourceforge.net/) for handling dates
* [maven](http://maven.apache.org/)