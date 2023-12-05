package com.tmproject.api.board.service;

import com.tmproject.api.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private BoardRepository boardRepository;

}
