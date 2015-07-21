package com.nagornyi.uc.dao;

import com.nagornyi.uc.entity.Feedback;

public interface IFeedbackDAO extends DAO<Feedback> {

    PaginationBatch<Feedback> getNextBatch(String startCursor, int limit);
}
