[![Build Status](https://travis-ci.org/NeatoRobotics/neato-sdk-android.svg?branch=master)](https://travis-ci.org/NeatoRobotics/neato-sdk-android)

# Neato SDK - Android

This is the [Neato Developer Network's](http://developers.neatorobotics.com) official Android SDK (Beta release).

The Neato Android SDK enables Android apps to easily communicate with Neato connected robots and use its various features.
The official Github repository can be found [here](https://github.com/NeatoRobotics/neato-sdk-android).

To boost your development, you can also check the *sample application*.

> This is a beta version. It is subject to change without prior notice.

## Preconditions

 - Create the Neato user account via the Neato portal or from the official Neato App
 - Link the robot to the user account via the official Neato App

## Setup
If you are using Gradle, add this dependency to your app build.gradle file:

``` groovy
compile 'com.neatorobotics.sdk.android:neato-sdk-android:0.10.0@aar'
```

and this repo reference to your project .gradle file:

``` groovy
allprojects {
    repositories {
        //other repos
        maven {
            url  "http://dl.bintray.com/neato/maven"
        }
    }
}
```

This permission is required to be added in your AndroidManifest.xml file:

``` xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Usage
The Neato SDK has 3 main roles:
1. Handling OAuth authentications
2. Simplifying users info interactions
3. Managing communication with Robots

These tasks are handled by different classes; You’ll mainly work with 3 of them: `NeatoAuthentication`, `NeatoRobot` and `NeatoUser`

### Authentication
The Neato SDK leverages on OAuth 2 to perform user authentication. The `NeatoAuthentication` class gives you all the needed means to easily perform a login through your apps. Let’s go through the steps needed to setup an app and perform the authentication.

#### 1. Creating a Schema URL
During the registration of your app on the Neato Developer Portal you have defined a `Redirect URI`. This is the URL where we redirect a user that completes a login with your Neato App Client ID. Your Android app must be able to handle this Redirect URI using a dedicated `Schema URL`. This is typically done declaring an Activity in your AndroidManifest.xml that can handle requests coming from this URI. For example, your login activity can be declared like this:

```xml
<activity
    android:name=".login.LoginActivity"
    android:launchMode="singleInstance">
    <intent-filter>
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <action android:name="android.intent.action.VIEW" />

        <data
            android:host="neato"
            android:scheme="my-neato-app" />
    </intent-filter>
</activity>
```

#### 2. Configuring the NeatoAuthentication class
In your sign in activity you obtain the instance of the `NeatoAuthentication` class like this:

```java
NeatoAuthentication neatoAuth = NeatoAuthentication.getInstance(this);
```

#### 3. Showing the Login page
You can now start the authentication flow invoking the `openLoginInBrowser` method:

```java
String REDIRECT_URI = "my-neato-app://neato";
String CLIENT_ID = "your_secret_client_id";
NeatoOAuth2Scope[] scopes = new NeatoOAuth2Scope[]{
    NeatoOAuth2Scope.CONTROL_ROBOTS,
    NeatoOAuth2Scope.PUBLIC_PROFILE,
    NeatoOAuth2Scope.MAPS
};
//we start the auth flow here
//later we'll receive the result in the onNewIntent activity method
neatoAuth.openLoginInBrowser(this,CLIENT_ID,REDIRECT_URI,scopes);
```
The user will be presented with a login page (on Chrome or another external browser) and when it completes the login it will be redirected to your App thanks to the `URL Schema` previously defined.

#### 4. Handling the Redirect URI
When the user finishes the login he is redirected to the previously configured login activity and the method onNewIntent is invoked. Here you can grab the OAuth access token if the login succeeded, otherwise you can show an error message.

```java
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    Uri uri = intent.getData();
    if (uri != null) {
        NeatoAuthenticationResponse response =
        neatoAuth.getOAuth2AuthResponseFromUri(uri);

        switch (response.getType()) {
            case TOKEN:
                //the token is automatically saved for you by the
                //NeatoAuthentication class, no need to save it!
                //Yay! we can now play with our robots!!
                break;
            case ERROR:
                //show auth error message
                break;
            default:
                //nothing to do here
        }
    }
}
```

#### 5. How to check if the user is already logged in
Sometimes you need to check if the user is already logged in, for example to skip directly to your robots page instead of passing through the login page. To check, simply do this:

```java
//here we're checking the access token
NeatoAuthenticationneatoAuth = NeatoAuthentication.getInstance(this);
if(neatoAuth.isAuthenticated()) {
    openRobotsActivity();
}else {
    //need to sign in first
    openLoginActivity();
}
```

#### 6. Create a custom AccessTokenDatasource
By default the Neato Android SDK use the *DefaultAccessTokenDatasource* to store and load the OAuth access token. This class stores the token into the app shared preferences. Although these preferences are typically known only by the app itself, it is possible that on rooted device someone can read these data. So, if you feel the need to secure the token, you can override the default access token datasource implementing the *AccessTokenDatasource* interface and these methods:

```java
public interface AccessTokenDatasource {
    void storeToken(String token, Date expires);
    String loadToken();
    void clearToken();
    boolean isTokenValid();
}
```

Once you have your custom access token datasource you have to retrieve the *NeatoAuthentication* instance using this alternative *getInstance()* method:

``` java
    /**
     * Use this method to get the singleton that use a custom
     * access token datasource.
     * @param context
     * @param accessTokenDatasource
     * @return the NeatoAuthentication instance
     */
    public static NeatoAuthentication getInstance(Context context,
                        AccessTokenDatasource accessTokenDatasource) {
        NeatoAuthentication auth = getInstance(context);
        auth.accessTokenDatasource = accessTokenDatasource;
        return auth;
    }
```

### Working with Users
Once the user is authenticated you can retrieve user information using the `NeatoUser` class:

```java
NeatoUser neatoUser = NeatoUser.getInstance(context);
```
#### Get user robots
To get the user robots list you can do this:

```java
neatoUser.loadRobots(new NeatoCallback<ArrayList<NeatoRobot>>(){
    @Override
    public void done(ArrayList<NeatoRobot> result) {
        super.done(result);
        //now you have the robot list
        }
    }

    @Override
    public void fail(NeatoError error) {
        super.fail(error);
        }
    }
});
```

*NeatoRobot* is a special class we have developed for you that can be used to directly invoke commands on the robot.

#### Get user info
If you want to retrieve the logged user email you can do this:

```java
neatoUser.getUserInfo(new NeatoCallback<JSONObject>(){
    @Override
    public void done(JSONObject result) {
        super.done(result);
        try {
            String userEmail = result.getString("email");
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void fail(NeatoError error) {
        super.fail(error);
    }
});
```

### Communicating with Robots
Now that you have the robots for an authenticated user, it’s time to communicate with them.
In the previous call you've seen how easy is to retrieve `NeatoRobot` instances for your current user. Those instances are ready to receive messages from your app (if the robots are online obviously).

#### The robot status
Before, we saw how to retrieve the robot list from the `NeatoUser` class. It is best practice to check the robot state before sending commands, otherwise the robot may be in a state that cannot accept the command and return an error code. To update/get the robot state do this:

```java
robot.updateRobotState(new NeatoCallback<Void>(){
    @Override
    public void done(Void result) {
        super.done(result);
        //the NeatoRobot state is now automatically filled
        RobotState state = robot.getState();
    }

    @Override
    public void fail(NeatoError error) {
        super.fail(error);
        //the robot maybe OFFLINE or in ERROR state
        //check the NeatoError for details
    }
});
```

#### Sending commands to a Robot
An online robot is ready to receive your commands like `startCleaning`. Some commands require parameters while others don't, see the API doc for details.

Pause cleaning doesn't require parameters:

```java
robot.pauseCleaning(new NeatoCallback<Void>(){
    @Override
    public void done(Void result) {
        super.done(result);
    }

    @Override
    public void fail(NeatoError error) {
        super.fail(error);
    }
});
```

Start cleaning requires parameters like the cleaning type (clean all house or spot), the cleaning mode (eco or turbo) and, in case of spot cleaning, the spot cleaning parameters (large or small area, 1x or 2x).

```java
String params = String.format(Locale.US,
                "{\"category\":%d,\"mode\":%d,\"modifier\":%d}",
                RobotConstants.ROBOT_CLEANING_CATEGORY_HOUSE,
                RobotConstants.ROBOT_CLEANING_MODE_ECO,
                RobotConstants.ROBOT_CLEANING_MODIFIER_NORMAL);

robot.startCleaning(params, new NeatoCallback<Void>(){
    @Override
    public void done(Void result) {
        super.done(result);
    }

    @Override
    public void fail(NeatoError error) {
        super.fail(error);
    }
});
```

#### Working with Robot schedule

To enable or disable all the robot schedule (note that schedule data are not deleted from the robot):

```java
if(robot.getState().isScheduleEnabled()) {
    robot.disableScheduling(new NeatoCallback<Void>() {
        @Override
        public void done(Void result) {
            super.done(result);
        }

        @Override
        public void fail(NeatoError error) {
            super.fail(error);
        }
    });
}else {
    robot.enableScheduling(new NeatoCallback<Void>() {
        @Override
        public void done(Void result) {
            super.done(result);
        }

        @Override
        public void fail(NeatoError error) {
            super.fail(error);
    });
}

```

To schedule house cleaning *every Wednesday at 15:00 in turbo mode*:

```java
ScheduleEvent everyWednesday = new ScheduleEvent();
    everyWednesday.mode = RobotConstants.ROBOT_CLEANING_MODE_TURBO;
    everyWednesday.day = 3;//0 is Sunday, 1 Monday and so on
    everyWednesday.startTime = "15:00";

ArrayList<ScheduleEvent> events = new ArrayList<>();
events.add(everyWednesday);
robot.setSchedule(events,new NeatoCallback<Void>(){
    @Override
    public void done(Void result) {
        super.done(result);
    }

    @Override
    public void fail(NeatoError error) {
        super.fail(error);
    }
});
```

*Note: not all robot models support the eco/turbo cleaning mode. You should check the robot available services before sending those parameters.*

#### Getting robot coverage maps

To retrieve the list of robot cleaning coverage maps:

```java
// check if the robot support this service
if (robot.hasService("maps")) {
    robot.getMaps(new NeatoCallback<JSONObject>() {
        @Override
        public void done(JSONObject result) {
            super.done(result);
            // now you can get a map id and retrieve the map details
            // to download the map image use the map "url" property
            // this second call is needed because 
            // the map urls expire after a while
            
            JSONArray maps = result.getJSONArray("maps");
            if (maps != null && maps.length() > 0) {
                String mapId = maps.getJSONObject(0).getString("id");
                getMapDetails(mapId);
            } else {
             // no maps available yet...
            }
        }
    });
}else {
    // the robot doesn't support this service...
}
```

To retrieve a specific map details:

```java
private void getMapDetails(String mapId) {
    robot.getMapDetails(mapId, new NeatoCallback<JSONObject>(){
        @Override
        public void done(JSONObject result) {
            super.done(result);
            String url = result.getString("url");
            showMapImage(url);
        }
    });   
}
```

You can now show the map image, for example using the very convenient Glide library:
```java
private void showMapImage(String url) {
    Glide.with(this).load(url).into(mapImageView);
}
```

#### Checking robot available services

Different robot models and versions have different features. So before sending commands to the robot you should check if that command is available on the robot. Otherwise the robot will responde with an error. You can check the available services on the robot looking into the *RobotState* class:

```java
HashMap<String, String> services = robot.getState().getAvailableServices();
```

In addition there are some utility methods you can use to check if the robot supports the services.

```java
//any version
boolean supportFindMe = robot.hasService("findMe");
```

```java
//specific service version
boolean supportManualCleaning = robot.hasService("manualCleaning","basic-1");
```

#### How to pass the NeatoRobot class through activities
*NeatoRobot* has two useful methods, *serialize()* and *deserialize()* that can be used in order to pass the robot through different activities.  For example in the first activity, say the robot list, we can click on the robot and pass it to the robot commands activity:

```java
public void onRobotClick(NeatoRobot robot) {
    Intent intent = new Intent(getContext(), RobotCommandsActivity.class);
    intent.putExtra("ROBOT", robot.serialize());
    startActivity(intent);
}
```

And in the onCreate method of the receiving activity:

```java
Bundle extras = getIntent().getExtras();
if (extras != null) {
    Robot serialized = (Robot)extras.getSerializable("ROBOT");
    this.robot = new NeatoRobot(getContext(),serialized);
}
```

In the same way you can save and restore your activities and fragments state when needed.
