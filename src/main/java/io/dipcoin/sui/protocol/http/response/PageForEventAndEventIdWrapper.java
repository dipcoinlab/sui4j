package io.dipcoin.sui.protocol.http.response;


import io.dipcoin.sui.model.Response;
import io.dipcoin.sui.model.event.PageForEventAndEventId;

/**
 * @author : Same
 * @datetime : 2025/9/25 12:08
 * @Description : Page_for_SuiObjectResponse_and_ObjectID response wrapper class.
 */
public class PageForEventAndEventIdWrapper extends Response<PageForEventAndEventId> {

    @Override
    public void setResult(PageForEventAndEventId result) {
        super.setResult(result);
    }

    @Override
    public PageForEventAndEventId getResult() {
        return super.getResult();
    }

}

