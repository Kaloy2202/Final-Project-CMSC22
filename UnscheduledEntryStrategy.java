import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.lang.Math;

public class UnscheduledEntryStrategy {
    public void scheduleUnscheduledEntries(Queue<UnscheduledEntry> unscheduledEntriesQueue, EntryManager entryManager, TimeblockManager timeblockManager) {

        int c = 0;

        while (!unscheduledEntriesQueue.isEmpty()){

            List<Integer> availableSlots = timeblockManager.getAvailableSlots();
            UnscheduledEntry unscheduledEntry = unscheduledEntriesQueue.peek();       
            int unitsPerTimeslot = unscheduledEntry.getUnitsPerTimeslot();
            int timeslot;
            if (availableSlots.get(0) == 0){
                timeslot = availableSlots.get(0);
            } else {
                timeslot = availableSlots.get(0) - 1;
            }
            

            int unitsRemaining = unscheduledEntry.getUnitsRemaining();

            if (unitsPerTimeslot >= unitsRemaining){          // last iteration

                LocalTime startTime = calculateTime(timeslot);
                LocalTime endTime = calculateTime(timeslot + unitsRemaining * 4);     //duration

                int startSlot = calculateMinutes(startTime);                     // update occupied timeslots
                int endSlot = calculateMinutes(endTime);
                int endDueSlot = calculateMinutes(unscheduledEntry.getDueTime());
                LocalTime endDueTime = unscheduledEntry.getDueTime();

                if (endDueSlot <= endSlot){
                    endSlot = endDueSlot;
                    endTime = endDueTime;
                }                
                entryManager.addScheduledEntry(startTime, endTime, unscheduledEntry.getName());             // add to the allEntries queue

                System.out.println("endDueSlot last" + endSlot);
                ArrayList<Integer> timeslotsToUpdate = new ArrayList<>();
                for (int i = startSlot; i <= endSlot; i++) {
                        timeslotsToUpdate.add(i % timeblockManager.getTotalSlots());
                }

                timeblockManager.updateTimeslots(timeslotsToUpdate);              
                System.out.println("removed" + unscheduledEntriesQueue.poll().getName());         // no units remaining, time to dequeue

                continue;

            } else {
                LocalTime startTime = calculateTime(timeslot);
                LocalTime endTime = calculateTime(timeslot + unitsPerTimeslot * 4);     //duration
                int startSlot = calculateMinutes(startTime);                            // update occupied timeslots
                int endSlot = calculateMinutes(endTime);
                int endDueSlot = calculateMinutes(unscheduledEntry.getDueTime());
                LocalTime endDueTime = unscheduledEntry.getDueTime();
                System.out.println(endDueTime);
                if (endDueSlot <= endSlot){
                    endSlot = endDueSlot;
                    endTime = endDueTime;
                }
               
                System.out.println("endDueSlot" + endSlot);
;
                ArrayList<Integer> timeslotsToUpdate = new ArrayList<>();
                for (int i = startSlot; i <= endSlot; i++) {
                        timeslotsToUpdate.add(i % timeblockManager.getTotalSlots());
                    }
                
                timeblockManager.updateTimeslots(timeslotsToUpdate);

                entryManager.addScheduledEntry(startTime, endTime, unscheduledEntry.getName());
                unscheduledEntry.decreaseUnits(unitsPerTimeslot);

                if (unitsRemaining > 0) {             // requeue
                    unscheduledEntriesQueue.poll();
                    entryManager.getUnscheduledEntriesQueue().add(unscheduledEntry);
                }
            }

            }
        }
    
        private static LocalTime calculateTime(int timeslot) {
            int hour = timeslot / 4;
            int minute = (timeslot % 4) * 15;
            return LocalTime.of(hour, minute);
        }
        public int calculateMinutes (LocalTime time) {
            return time.getHour() * 4 + time.getMinute() / 15;  //returns the numeric value of the timeslot within 0 to 95
        }
    

}
