package tr.edu.boun.swe574.fsn.web.wicket.profile.searchUsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import tr.edu.boun.swe574.fsn.web.common.FsnRoles;
import tr.edu.boun.swe574.fsn.web.common.constants.Constants;
import tr.edu.boun.swe574.fsn.web.common.constants.ResultCode;
import tr.edu.boun.swe574.fsn.web.common.info.SearchUserForm;
import tr.edu.boun.swe574.fsn.web.common.ws.WSCaller;
import tr.edu.boun.swe574.fsn.web.wicket.FsnSession;
import tr.edu.boun.swe574.fsn.web.wicket.common.BasePage;
import tr.edu.boun.swe574.fsn.web.wicket.profile.userProfile.UserProfile;
import edu.boun.swe574.fsn.common.client.network.SearchForUsersResponse;
import edu.boun.swe574.fsn.common.client.network.UserInfo;

@AuthorizeInstantiation(value = {FsnRoles.USER})
public class SearchUsers extends BasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7761888071957256271L;
	
	private static Logger logger = Logger.getLogger(SearchUsers.class); 
	
	private TextField<String>             searchString;
	private Form<Void> form;
	private DataTable<UserInfo>  dataTable;
	private SubmitLink       btnSearch;
	private List<UserInfo> userList;
	
	private SearchUserForm formInfo = new SearchUserForm();
	
	public SearchUsers() {
		
		userList = new ArrayList<UserInfo>();
		
		searchString  = new TextField<String>("txtSearchString", new PropertyModel<String>(formInfo, "searchString"));
		
		//http://www.wicket-library.com/wicket-examples/repeater/wicket/bookmarkable/org.apache.wicket.examples.repeater.GridViewPage;jsessionid=FFA39F59CF71FA7AB7E0FD09F2AAFC57?0
		//see GridView Example - demonstrates a grid view
		//http://www.wicket-library.com/wicket-examples/repeater/
		dataTable = getUserTable();
		dataTable.setVisible(false);
		dataTable.setOutputMarkupId(true);
		
		form = new Form<Void>("form");
		
		btnSearch = new SubmitLink("btnSearch") {
			 /**
			 * 
			 */
			private static final long serialVersionUID = 3756373317543372536L;

			public void onSubmit() {
				//get users
				if(logger.isDebugEnabled()) {
					logger.debug("Getting user by the criteria:" + searchString.getModelObject());
				}
				
				SearchForUsersResponse response = WSCaller.getNetworkService().searchForUsers(FsnSession.getInstance().getUser().getToken(), searchString.getModelObject());
				
				if(response.getResultCode() == ResultCode.FAILURE.getCode()) {
					logger.error("Nwtwork service returned error code:" + response.getErrorCode());
					error("Something went wrong.");
					return;
				}
				List<UserInfo> listOfUsers = response.getUserList();
				if(listOfUsers != null && !listOfUsers.isEmpty()) {
					userList.clear();
					userList.addAll(listOfUsers);
//					Rhoda Penmark
					UserInfo i = new UserInfo();
					i.setName("Rhoda");
					i.setSurname("Penmark");
					userList.add(i);
				}
				dataTable.setVisible(true);
			 }
		};
		
		form.add(btnSearch);
		form.add(searchString);
		form.add(dataTable);
		
		searchString.setRequired(true);
		
		add(form);
	}
	
	private DataTable<UserInfo> getUserTable() {
    	
        List<IColumn<UserInfo>> columns = new ArrayList<IColumn<UserInfo>>();
        

        columns.add(new PropertyColumn<UserInfo>(
                new Model<String>("Name"),"name") {

            /**
             * 
             */
            private static final long serialVersionUID = 5220247060301534590L;
            
            @Override
            public void populateItem(Item<ICellPopulator<UserInfo>> item,
                                     String componentId,
                                     IModel<UserInfo> rowModel) {

                Fragment frgUser = new Fragment(componentId,
                                                     "frgUser",
                                                     SearchUsers.this,
                                                     rowModel);
                item.add(frgUser);

                final UserInfo userInfo = rowModel.getObject();
                SubmitLink lnkUser = new SubmitLink("lnkUser") {
                	
                	/**
					 * 
					 */
					private static final long serialVersionUID = -8580276547405546079L;

					public void onSubmit() {
                		setResponsePage(new UserProfile(userInfo.getEmail()));
                	};
                };
                
                Label lblUserName = new Label("lblUserName", userInfo.getName() + " " + userInfo.getSurname());
                lnkUser.add(lblUserName);
                
                //TODO set profile message
                
                frgUser.add(lnkUser);
                
            }
            
        });


        SortableDataProvider<UserInfo> provider = new SortableDataProvider<UserInfo>() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public Iterator<UserInfo> iterator(int first, int count) {
                if (getSort() != null) {
                    Collections.sort(userList, new Comparator<Object>() {

                        public int compare(Object o1, Object o2) {
                            PropertyModel<Comparable<Object>> model1 = new PropertyModel<Comparable<Object>>(o1,
                                                                                                             getSort().getProperty());
                            PropertyModel<Comparable<Object>> model2 = new PropertyModel<Comparable<Object>>(o2,
                                                                                                             getSort().getProperty());

                            int result;
                            if (model1.getObject() == null) {
                                result = 1;
                            } else if(model2.getObject() == null){
                                result = -1;
                            }
                            else {
                                result = model1.getObject().compareTo(model2.getObject());
                            }
                            
                            if (!getSort().isAscending()) {
                                result = -result;
                            }

                            return result;
                        }
                    });
                }
                return userList.subList(first, first + count).iterator();
            }

            public int size() {
                return userList.size();
            }

            @Override
            public IModel<UserInfo> model(final UserInfo user) {
                return new LoadableDetachableModel<UserInfo>() {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = -7659348043571352653L;

                    @Override
                    protected UserInfo load() {
                        return user;
                    }

                };
            }
        };

        DataTable<UserInfo> dataTable = new DataTable<UserInfo>(
                "resultTable", columns, provider, Constants.GUI_PAGE_SIZE) {

            /**
                     * 
                     */
            private static final long serialVersionUID = -8189804759594690615L;

            protected Item<UserInfo> newRowItem(String id, int index,
                                                                   IModel<UserInfo> model) {
                return new OddEvenItem<UserInfo>(id, index, model);
            }

        };

        NavigationToolbar navigationToolbar = new NavigationToolbar(dataTable) {

            /**
             * 
             */
            private static final long serialVersionUID = -5313020381701491013L;

            protected void onConfigure() {
                super.onConfigure();
                setVisible(true);
            }
        };
        dataTable.addBottomToolbar(navigationToolbar);

        //dataTable.addTopToolbar(new HeadersToolbar(dataTable, provider));
        dataTable.setOutputMarkupId(true);
        return dataTable;
    }

}
