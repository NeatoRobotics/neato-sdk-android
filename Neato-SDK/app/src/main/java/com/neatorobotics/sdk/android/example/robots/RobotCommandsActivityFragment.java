package com.neatorobotics.sdk.android.example.robots;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.neatorobotics.sdk.android.NeatoCallback;
import com.neatorobotics.sdk.android.NeatoError;
import com.neatorobotics.sdk.android.NeatoRobot;
import com.neatorobotics.sdk.android.example.R;
import com.neatorobotics.sdk.android.models.CleaningMap;
import com.neatorobotics.sdk.android.models.Robot;
import com.neatorobotics.sdk.android.models.RobotState;
import com.neatorobotics.sdk.android.models.ScheduleEvent;
import com.neatorobotics.sdk.android.nucleo.RobotConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class RobotCommandsActivityFragment extends Fragment {

    protected NeatoRobot robot;

    private Button houseCleaning, mapCleaning, spotCleaning, pauseCleaning,
                    stopCleaning, resumeCleaning, returnToBaseCleaning, findMe,
                    enableDisableScheduling, scheduleEveryWednesday, getScheduling,
                    maps;

    private ImageView mapImageView;

    public RobotCommandsActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("ROBOT",robot.serialize());
    }

    private void restoreState(Bundle inState) {
        robot = new NeatoRobot(getContext(),(Robot) inState.getSerializable("ROBOT"));
        updateUIButtons();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_robot_commands, container, false);

        houseCleaning = (Button)rootView.findViewById(R.id.houseCleaning);
        spotCleaning = (Button)rootView.findViewById(R.id.spotCleaning);
        mapCleaning = (Button)rootView.findViewById(R.id.mapCleaning);
        pauseCleaning = (Button)rootView.findViewById(R.id.pauseCleaning);
        stopCleaning = (Button)rootView.findViewById(R.id.stopCleaning);
        resumeCleaning = (Button)rootView.findViewById(R.id.resumeCleaning);
        returnToBaseCleaning = (Button)rootView.findViewById(R.id.returnToBaseCleaning);
        findMe = (Button)rootView.findViewById(R.id.findMe);
        enableDisableScheduling = (Button)rootView.findViewById(R.id.enableDisableScheduling);
        scheduleEveryWednesday = (Button)rootView.findViewById(R.id.wednesdayScheduling);
        getScheduling = (Button)rootView.findViewById(R.id.getScheduling);
        maps = (Button) rootView.findViewById(R.id.maps);
        mapImageView = (ImageView) rootView.findViewById(R.id.mapImage);

        spotCleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeSpotCleaning();
            }
        });

        houseCleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeHouseCleaning();
            }
        });

        mapCleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeMapCleaning();
            }
        });

        pauseCleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executePause();
            }
        });

        stopCleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeStop();
            }
        });

        returnToBaseCleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeReturnToBase();
            }
        });

        resumeCleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeResumeCleaning();
            }
        });

        findMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeFindMe();
            }
        });

        enableDisableScheduling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDisableScheduling();
            }
        });

        scheduleEveryWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleEveryWednesday();
            }
        });

        getScheduling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getScheduling();
            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMaps();
            }
        });

        return rootView;
    }

    private void updateUIButtons(NeatoError error) {

        if(error != null) {
            Toast.makeText(getContext(), error.name(), Toast.LENGTH_SHORT).show();
        }

        updateUIButtons();
    }

    private void updateUIButtons() {
        if(robot != null && robot.getState() != null) {
            houseCleaning.setEnabled(robot.getState().isStartAvailable());
            spotCleaning.setEnabled(robot.getState().isStartAvailable());
            pauseCleaning.setEnabled(robot.getState().isPauseAvailable());
            stopCleaning.setEnabled(robot.getState().isStopAvailable());
            resumeCleaning.setEnabled(robot.getState().isResumeAvailable());
            returnToBaseCleaning.setEnabled(robot.getState().isGoToBaseAvailable());
        }else {
            houseCleaning.setEnabled(false);
            spotCleaning.setEnabled(false);
            pauseCleaning.setEnabled(false);
            stopCleaning.setEnabled(false);
            resumeCleaning.setEnabled(false);
            returnToBaseCleaning.setEnabled(false);
        }
    }

    private void executePause() {
        if(robot != null) {
            robot.pauseCleaning(new NeatoCallback<RobotState>(){
                @Override
                public void done(RobotState result) {
                    super.done(result);
                    updateUIButtons();
                }

                @Override
                public void fail(NeatoError error) {
                    super.fail(error);
                    updateUIButtons(error);
                }
            });
        }
    }

    private void executeResumeCleaning() {
        if(robot != null) {
            robot.resumeCleaning(new NeatoCallback<RobotState>(){
                @Override
                public void done(RobotState result) {
                    super.done(result);
                    updateUIButtons();
                }

                @Override
                public void fail(NeatoError error) {
                    super.fail(error);
                    updateUIButtons(error);
                }
            });
        }
    }

    private void executeHouseCleaning() {
        if(robot != null) {
            HashMap<String, String> params = new HashMap<>();
                params.put(RobotConstants.CLEANING_MODE_KEY, RobotConstants.ROBOT_CLEANING_MODE_TURBO+"");

            robot.startHouseCleaning(params, new NeatoCallback<RobotState>(){
                @Override
                public void done(RobotState result) {
                    super.done(result);
                    updateUIButtons();
                }

                @Override
                public void fail(NeatoError error) {
                    super.fail(error);
                    updateUIButtons(error);
                }
            });
        }
    }

    private void executeMapCleaning() {
        if(robot != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put(RobotConstants.CLEANING_MODE_KEY, RobotConstants.ROBOT_CLEANING_MODE_TURBO+"");

            robot.startFloorPlanCleaning(params, new NeatoCallback<RobotState>(){
                @Override
                public void done(RobotState result) {
                    super.done(result);
                    updateUIButtons();
                }

                @Override
                public void fail(NeatoError error) {
                    super.fail(error);
                    updateUIButtons(error);
                }
            });
        }
    }

    private void executeReturnToBase() {
        if(robot != null) {
            robot.goToBase(new NeatoCallback<RobotState>(){
                @Override
                public void done(RobotState result) {
                    super.done(result);
                    updateUIButtons();
                }

                @Override
                public void fail(NeatoError error) {
                    super.fail(error);
                    updateUIButtons(error);
                }
            });
        }
    }

    private void executeFindMe() {
        if(robot != null) {
            //check if the robot support this service
            if(robot.hasService("findMe")) {
                robot.findMe(new NeatoCallback<Void>() {
                    @Override
                    public void done(Void result) {
                        super.done(result);
                        updateUIButtons();
                    }

                    @Override
                    public void fail(NeatoError error) {
                        super.fail(error);
                        updateUIButtons(error);
                    }
                });
            }else {
                Toast.makeText(getContext(),"The robot doesn't support this service.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableDisableScheduling() {
        if(robot != null) {
            if(robot.getState().isScheduleEnabled()) {
                robot.disableScheduling(new NeatoCallback<Void>() {
                    @Override
                    public void done(Void result) {
                        super.done(result);
                        updateUIButtons();
                        Toast.makeText(getContext(),"Scheduling disabled!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void fail(NeatoError error) {
                        super.fail(error);
                        updateUIButtons(error);
                        Toast.makeText(getContext(),"Impossible to disable scheduling.",Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                robot.enableScheduling(new NeatoCallback<Void>() {
                    @Override
                    public void done(Void result) {
                        super.done(result);
                        updateUIButtons();
                        Toast.makeText(getContext(),"Scheduling enabled!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void fail(NeatoError error) {
                        super.fail(error);
                        updateUIButtons(error);
                        Toast.makeText(getContext(),"Impossible to enable scheduling.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void scheduleEveryWednesday() {
        if(robot != null) {
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
                    updateUIButtons();
                    Toast.makeText(getContext(),"Yay! Schedule programmed.",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void fail(NeatoError error) {
                    super.fail(error);
                    updateUIButtons(error);
                    Toast.makeText(getContext(),"Oops! Impossible to set schedule.",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getScheduling() {
        if(robot != null) {
            robot.getSchedule(new NeatoCallback<ArrayList<ScheduleEvent>>(){
                @Override
                public void done(ArrayList<ScheduleEvent> result) {
                    super.done(result);
                    updateUIButtons();
                    if(result != null) {
                        Toast.makeText(getContext(), "The robot has " + result.size() + " scheduled events.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void fail(NeatoError error) {
                    super.fail(error);
                    updateUIButtons(error);
                    Toast.makeText(getContext(),"Oops! Impossible to get schedule.",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getMapDetails(String mapId) {
        if(robot != null) {
            robot.getMapDetails(mapId, new NeatoCallback<CleaningMap>(){
                @Override
                public void done(CleaningMap map) {
                    super.done(map);
                    showMapImage(map.getUrl());
                }

                @Override
                public void fail(NeatoError error) {
                    super.fail(error);
                    Toast.makeText(getContext(), "Oops! Impossible to get map details.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showMapImage(String url) {
        Glide.with(this).load(url).into(mapImageView);
    }

    private void getMaps() {
        if(robot != null) {
            //check if the robot support this service
            if (robot.hasService("maps")) {
                robot.getMaps(new NeatoCallback<List<CleaningMap>>() {
                    @Override
                    public void done(List<CleaningMap> maps) {
                        super.done(maps);
                        updateUIButtons();
                        if (maps != null && maps.size() > 0) {
                            // now you can get a map id and retrieve the map details
                            // to download the map image use the map "url" property
                            // this second call is needed because the map urls expire after a while
                            getMapDetails(maps.get(0).getId());

                        }else {
                            Toast.makeText(getContext(), "No maps available yet. Complete at least one house cleaning to view maps.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(NeatoError error) {
                        super.fail(error);
                        updateUIButtons();
                        Toast.makeText(getContext(), "Oops! Impossible to get robot maps.", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(getContext(),"The robot doesn't support this service.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void executeStop() {
        if(robot != null) {
            robot.stopCleaning(new NeatoCallback<RobotState>(){
                @Override
                public void done(RobotState result) {
                    super.done(result);
                    updateUIButtons();
                }

                @Override
                public void fail(NeatoError error) {
                    super.fail(error);
                    updateUIButtons(error);
                }
            });
        }
    }

    private void executeSpotCleaning() {
        if(robot != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put(RobotConstants.CLEANING_MODE_KEY, RobotConstants.ROBOT_CLEANING_MODE_ECO+"");
            params.put(RobotConstants.CLEANING_AREA_SPOT_HEIGHT_KEY, RobotConstants.ROBOT_CLEANING_SPOT_SIZE_LARGE+"");
            params.put(RobotConstants.CLEANING_AREA_SPOT_WIDTH_KEY, RobotConstants.ROBOT_CLEANING_SPOT_SIZE_LARGE+"");

            robot.startSpotCleaning(params, new NeatoCallback<RobotState>(){
                @Override
                public void done(RobotState result) {
                    super.done(result);
                    updateUIButtons();
                }

                @Override
                public void fail(NeatoError error) {
                    super.fail(error);
                    updateUIButtons(error);
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            restoreState(savedInstanceState);
        }
    }

    public void injectRobot(Robot robot) {
        this.robot = new NeatoRobot(getContext(),robot);
        updateUIButtons();
    }

    public void reloadRobotState() {
        robot.updateRobotState(new NeatoCallback<Void>(){
            @Override
            public void done(Void result) {
                super.done(result);
                updateUIButtons();
            }

            @Override
            public void fail(NeatoError error) {
                super.fail(error);
                updateUIButtons();
            }
        });
    }
}
