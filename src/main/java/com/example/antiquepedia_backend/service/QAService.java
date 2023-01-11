package com.example.antiquepedia_backend.service;

import com.example.antiquepedia_backend.Entity.Answer;
import com.example.antiquepedia_backend.Entity.Question;

public interface QAService {
    public Answer answer(Question question);
}