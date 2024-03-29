package edu.boun.swe574.fsn.mobile.ws.response;

import java.util.List;

import org.ksoap2.serialization.SoapObject;

import edu.boun.swe574.fsn.mobile.util.AndroidUtil;
import edu.boun.swe574.fsn.mobile.ws.dto.RecipeInfo;

public class ResponseGetRecipeFeed extends BaseResponse {
	private List<RecipeInfo> recipeList;

	public ResponseGetRecipeFeed(SoapObject object) {
		super(object);
		if (object != null) {
			this.recipeList = AndroidUtil.convertSoapObjectToList(object, "return.recipeList", RecipeInfo.class);
		}
	}

	public List<RecipeInfo> getRecipeList() {
		return recipeList;
	}

	public void setRecipeList(List<RecipeInfo> recipeList) {
		this.recipeList = recipeList;
	}
}
