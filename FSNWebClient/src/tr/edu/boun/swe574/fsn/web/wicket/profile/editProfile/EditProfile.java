package tr.edu.boun.swe574.fsn.web.wicket.profile.editProfile;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

import tr.edu.boun.swe574.fsn.web.common.info.UserInfoForm;
import tr.edu.boun.swe574.fsn.web.common.info.WebUser;
import tr.edu.boun.swe574.fsn.web.wicket.FsnSession;
import tr.edu.boun.swe574.fsn.web.wicket.common.BasePanel;

public class EditProfile extends BasePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5680758832419784439L;
	
	private static Logger logger = Logger.getLogger(EditProfile.class);

	private Form<Void> form;
	
	private AjaxSubmitLink lnkUpdate;
	private AjaxSubmitLink lnkCancel;
	private FeedbackPanel feedbackPanel = new FeedbackPanel("feedBackPanel");
	
	private TextField<String> name;
	private TextField<String> surname;
	private TextField<String> location;
	private TextArea<String> profileMessage;
	private DateTextField birthday;
	
	private UserInfoForm formInfo = new UserInfoForm();
	
	public EditProfile(String id, final ModalWindow mwEditProfile, final UserInfoForm profile) { 
		super(id);
		
		form = new Form<Void>("form");
		
		WebUser user = FsnSession.getInstance().getUser();
		
		name = new TextField<String>("name", new PropertyModel<String>(formInfo, "firstName"));
		name.setRequired(true);
		name.setModelObject(user.getFirstName());
		form.add(name);
		
		surname = new TextField<String>("surname", new PropertyModel<String>(formInfo, "lastName"));
		surname.setRequired(true);
		surname.setModelObject(user.getLastName());
		form.add(surname);
		
		location = new TextField<String>("location", new PropertyModel<String>(formInfo, "location"));
		location.setModelObject(profile.getLocation());
		form.add(location);
		
        birthday = DateTextField.forDatePattern("birthday", "yyyy-MM-dd");
        birthday.setModel(new PropertyModel<Date>(formInfo, "birthdate"));
        birthday.setModelObject(profile.getBirthdate());
        birthday.add(new DatePicker() {

            /**
             * 
             */
            private static final long serialVersionUID = -7088155317768431971L;

            @Override
            protected boolean enableMonthYearSelection() {
                return true;
            }
        });
		
		form.add(birthday);
		
		profileMessage = new TextArea<String>("profileMessage", new PropertyModel<String>(formInfo, "profileMessage"));
		profileMessage.setModelObject(profile.getProfileMessage());
		form.add(profileMessage);

		lnkUpdate = new AjaxSubmitLink("lnkUpdate") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4091528668903964731L;
			
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            	//call ws
				mwEditProfile.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                if (logger.isInfoEnabled()) {
                    logger.info("An error has been occurred on Fraud group comment button");
                }
            }
		};
		
		lnkCancel = new AjaxSubmitLink("lnkCancel") {
			
            /**
			 * 
			 */
			private static final long serialVersionUID = 3908411552647518543L;

			@Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				mwEditProfile.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                if (logger.isInfoEnabled()) {
                    logger.info("An error has been occurred on Fraud group comment button");
                }
            }
		};
		
		form.add(lnkUpdate);
		form.add(lnkCancel);
		add(form);
		form.add(feedbackPanel);

	}
	
}
