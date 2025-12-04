package ma.nassnewsapp.backend.dto;

import java.util.List;

public class NewsApiResponseDto {
    public String status;
    public int totalResults;
    public List<ArticleDto> articles;
}