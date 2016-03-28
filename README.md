# Siteview
Real-time web usage statistics.

![ScreenShot](/screenshots/ss-1.png)

## How it Works

The application includes the following components:

* Web route to serve the UI (dashboard and related static content)
* Tracking image route to serve the transparent 1x1 pixel GIF image, which is included in monitored sites
* Event bus route to push statistics to the dashboard

## Integration

Integration (start to monitor sites) is quite easy. Let's say you start up the application on port `9090` on a machine with an IP address of `192.168.0.1`.

On your web site you would add the following `img` tag:

```
http://192.168.0.1:9090/img
```

To monitor the generated feed, you would browse to:

```
http://192.168.0.1:9090/html/
```

Of course, in a real world scenario, the tracking image route would be published to an external interface taking into account appropriate firewalls and load balancers. Be sure not to **cache** the tracking image though.

## Installation

Use the steps below to install the application.

1. Clone the repository:

```
$ git clone https://github.com/tutikka/siteview.git
```

2. Change to the cloned repository directory:

```
$ cd siteview
```

3. Build the application:

```
$ ant
```

## Usage

Use the steps below to start the application (after building it).

1. Change to the generated `dist` directory:

```
$ cd dist
```

2. Use the provided `siteview.sh` script to start the application (CTRL-C to stop):

```
$ sh ./siteview.sh
```

## Credits

The real-time hits/second feed seen on the dashboard uses [Highcharts](http://www.highcharts.com).

The backbone of the application uses [Vertx](http://vertx.io) to route incoming request and deliver events to the UI.

Operating systems and browsers are detected from the User-Agent header with [UADetector](http://uadetector.sourceforge.net).

Location-specific information is detected based on IP addresses using [MaxMind Open Source Data and APIs](https://www.maxmind.com/en/open-source-data-and-api-for-ip-geolocation).

The cool font seen on the dashboard is [Unica One](https://www.google.com/fonts/specimen/Unica+One) by Eduardo Tunni.