package org.biobank.platedecoder.service.fsm;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FsmTest {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(FsmTest.class);

   private static enum StateId {
      STATE1,
      STATE2,
      STATE3;
   }

   private static enum ChoicePointId {
      CHOICE1
   }

   private static enum Event {
      EVENT1,
      EVENT2,
      EVENT3;
   }

   private boolean choiceValue = true;

   private Fsm<StateId, ChoicePointId, Event> createFsm(final List<StateId> stateCheck) {
      Fsm<StateId, ChoicePointId, Event> fsm = new Fsm<>();

      fsm.addState(StateId.STATE1);
      fsm.addState(StateId.STATE2);

      fsm.addTransition(Event.EVENT1, StateId.STATE1, StateId.STATE2, () -> {
            stateCheck.add(StateId.STATE1);
         });
      fsm.addTransition(Event.EVENT2, StateId.STATE2, StateId.STATE1, () -> {
            stateCheck.add(StateId.STATE2);
         });

      return fsm;
   }

   @Test
   public void runnablesExecuted() {
      List<StateId> stateCheck = new ArrayList<>();
      Fsm<StateId, ChoicePointId, Event> fsm = createFsm(stateCheck);

      fsm.feedEvent(Event.EVENT1);
      fsm.feedEvent(Event.EVENT2);

      Assert.assertEquals(2, stateCheck.size());
      Assert.assertEquals(StateId.STATE1, stateCheck.get(0));
      Assert.assertEquals(StateId.STATE2, stateCheck.get(1));
   }

   @Test
   public void invalidEvents() {
      List<StateId> stateCheck = new ArrayList<>();
      Fsm<StateId, ChoicePointId, Event> fsm = createFsm(stateCheck);

      fsm.feedEvent(Event.EVENT2);
      fsm.feedEvent(Event.EVENT1);
   }

   @Test(expected = IllegalStateException.class)
   public void stateWithNoTransitionFailsValidation() {
      Fsm<StateId, ChoicePointId, Event> fsm = new Fsm<>();
      fsm.addState(StateId.STATE1);
      fsm.validate();
   }

   @Test(expected = IllegalArgumentException.class)
   public void addSameStateTwiceFails() {
      Fsm<StateId, ChoicePointId, Event> fsm = new Fsm<>();
      fsm.addState(StateId.STATE1);
      fsm.addState(StateId.STATE1);
   }

   @Test(expected = IllegalStateException.class)
   public void choicepointWithNoTransitionFailsValidation() {
      Fsm<StateId, ChoicePointId, Event> fsm = new Fsm<>();
      fsm.addChoicepoint(ChoicePointId.CHOICE1, () -> false);
      fsm.validate();
   }

   @Test(expected = IllegalArgumentException.class)
   public void addSameChoicepointTwiceFails() {
      Fsm<StateId, ChoicePointId, Event> fsm = new Fsm<>();
      fsm.addChoicepoint(ChoicePointId.CHOICE1, () -> false);
      fsm.addChoicepoint(ChoicePointId.CHOICE1, () -> false);
   }

   @Test
   public void choicePoints() {
      List<StateId> stateCheck = new ArrayList<>();
      List<ChoicePointId> choicePointCheck = new ArrayList<>();
      Fsm<StateId, ChoicePointId, Event> fsm = new Fsm<>();

      fsm.addState(StateId.STATE1);
      fsm.addState(StateId.STATE2);
      fsm.addState(StateId.STATE3);

      // TODO add transition from choicepoint ot choicepoint

      fsm.addChoicepoint(ChoicePointId.CHOICE1, () -> {
            return choiceValue;
         });

      fsm.addTransitionToChoice(
         Event.EVENT1,
         StateId.STATE1,
         ChoicePointId.CHOICE1, () -> {
            stateCheck.add(StateId.STATE1);
         });
      fsm.addTransitionFromChoiceToState(
         ChoicePointId.CHOICE1,
         false,
         StateId.STATE2,
         () -> {
            choicePointCheck.add(ChoicePointId.CHOICE1);
         });
      fsm.addTransitionFromChoiceToState(
         ChoicePointId.CHOICE1,
         true,
         StateId.STATE3,
         () -> {
            choicePointCheck.add(ChoicePointId.CHOICE1);
         });
      fsm.addTransition(
         Event.EVENT2,
         StateId.STATE2,
         StateId.STATE1,
         () -> {
            stateCheck.add(StateId.STATE2);
         });
      fsm.addTransition(
         Event.EVENT3,
         StateId.STATE3,
         StateId.STATE1,
         () -> {
            stateCheck.add(StateId.STATE3);
         });

      fsm.feedEvent(Event.EVENT1);
      fsm.feedEvent(Event.EVENT3);
      choiceValue = false;
      fsm.feedEvent(Event.EVENT1);
      fsm.feedEvent(Event.EVENT2);

      Assert.assertEquals(4, stateCheck.size());
      Assert.assertEquals(StateId.STATE1, stateCheck.get(0));
      Assert.assertEquals(StateId.STATE3, stateCheck.get(1));
      Assert.assertEquals(StateId.STATE1, stateCheck.get(2));
      Assert.assertEquals(StateId.STATE2, stateCheck.get(3));

      Assert.assertEquals(2, choicePointCheck.size());
      Assert.assertEquals(ChoicePointId.CHOICE1, choicePointCheck.get(0));
      Assert.assertEquals(ChoicePointId.CHOICE1, choicePointCheck.get(1));
   }
}
