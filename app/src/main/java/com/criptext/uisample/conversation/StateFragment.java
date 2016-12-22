package com.criptext.uisample.conversation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.criptext.monkeykitui.conversation.ConversationsList;
import com.criptext.monkeykitui.conversation.MonkeyConversation;
import com.criptext.monkeykitui.util.MonkeyFragmentManager;

import java.util.Stack;

/**
 * Created by gesuwall on 12/21/16.
 */
public class StateFragment extends Fragment {
    public Stack<MonkeyFragmentManager.FragmentTypes> mkFragmentStack;
    public ConversationsList conversationsList;
    public MonkeyConversation activeConversation;

    public static StateFragment newStateFragment (Context ctx) {
        StateFragment fragment = new StateFragment();
        fragment.mkFragmentStack = new Stack<>();
        fragment.conversationsList = new ConversationsList(new FakeConversations().getAll(ctx));
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
