package com.tmproject.api.board.dto;

import com.tmproject.api.board.entity.Board;

import java.util.List;

public class BoardListDto {

    private List<Board> boards;
    private String message;
    private int statusCode;

    public BoardListDto(String message, int statusCode, List<Board> boards) {
        this.message = message;
        this.statusCode = statusCode;
        this.boards = boards;
    }

    public List<Board> getBoards() {
        return boards;
    }

    public void setBoards(List<Board> boards) {
        this.boards = boards;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}