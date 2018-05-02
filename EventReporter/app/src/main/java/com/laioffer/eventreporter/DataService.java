package com.laioffer.eventreporter;

import com.laioffer.eventreporter.artifacts.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yukening on 2018/3/4.
 */

public class DataService {
    /**
     * Fake all the event data for now. We will refine this and connect
     * to our backend later.
     */
    public static List<Event> getEventData() {
        List<Event> eventData = new ArrayList<Event>();
        for (int i = 0; i < 12; ++i) {
            eventData.add(
                    new Event("Event", "1184 W valley Blvd, CA 90101",
                            "This is a huge event"));
        }
        return eventData;
    }

}
