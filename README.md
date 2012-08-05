HubStep.co
==========

Main repo for hubstep.co and anything server side.


Running
=======

You need SBT to run the application. You can use [sbt-extras](https://github.com/paulp/sbt-extras) to get started with SBT. To run:

    sbt

To run tests:

    sbt test

To start in semi-production:

    sbt run

To start in test mode

    run -Dconfig.file=conf/test.conf


To deploy to heroku
===================

Currently, it is running against my account. You can request permission to the heroku repo and then you will be able
to push.

Prior to release, ensure you test the project with similar setup as heroku. For that you need postgresql installed locally.

Then run ```foreman start``` after a ```sbt stage```
