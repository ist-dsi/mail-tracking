package module.mailtracking.presentationTier.layout;

import org.apache.struts.action.ActionForward;

import myorg.presentationTier.Context;

public class EmptyContextLayout extends Context {
    
    public EmptyContextLayout() {
	super();
    }

    public EmptyContextLayout(String path) {
	super(path);
    }

    @Override
    public ActionForward forward(String forward) {
	return new ActionForward(forward);
    }
}
