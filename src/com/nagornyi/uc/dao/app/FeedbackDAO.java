package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.nagornyi.uc.dao.IFeedbackDAO;
import com.nagornyi.uc.dao.PaginationBatch;
import com.nagornyi.uc.entity.Feedback;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;

public class FeedbackDAO extends EntityDAO<Feedback> implements IFeedbackDAO {

    @Override
    protected Feedback createDAOEntity(Entity entity) {
        return new Feedback(entity);
    }

    @Override
    protected String getKind() {
        return Feedback.class.getSimpleName();
    }

    @Override
    public PaginationBatch<Feedback> getNextBatch(String startCursor, int limit) {
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(limit);
        if (startCursor != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
        }

        Query query = new Query(getKind())
                .addSort("date", Query.SortDirection.DESCENDING);

        QueryResultList<Entity> results = datastore.prepare(query).asQueryResultList(fetchOptions);
        PaginationBatch<Feedback> result = new PaginationBatch<>(results.getCursor().toWebSafeString());
        for (Entity entity: results) {
            result.addEntity(createDAOEntity(entity));
        }

        return result;
    }
}
