package com.starlit.stockservice.config;

import com.starlit.stockservice.entity.StockDailyPrice;
import com.starlit.stockservice.entity.StockMaster;
import com.starlit.stockservice.repository.StockDailyPriceRepository;
import com.starlit.stockservice.repository.StockMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 샘플 데이터 초기화.
 *
 * <p>개발 환경에서만 동작하며, DB에 데이터가 없을 때 대표 종목과 시세를 자동 생성한다.
 * 외부 API 연동 전까지 프론트엔드 개발용으로 사용된다.</p>
 */
@Slf4j
@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final StockMasterRepository stockMasterRepository;
    private final StockDailyPriceRepository priceRepository;

    public DataInitializer(StockMasterRepository stockMasterRepository,
                           StockDailyPriceRepository priceRepository) {
        this.stockMasterRepository = stockMasterRepository;
        this.priceRepository = priceRepository;
    }

    @Override
    public void run(String... args) {
        if (stockMasterRepository.count() > 0) {
            log.info("종목 데이터 이미 존재 — 초기화 생략");
            return;
        }

        log.info("샘플 종목 데이터 초기화 시작");

        List<StockMaster> stocks = createSampleStocks();
        stockMasterRepository.saveAll(stocks);

        createSamplePrices();

        log.info("샘플 종목 {}건, 시세 데이터 초기화 완료", stocks.size());
    }

    private List<StockMaster> createSampleStocks() {
        LocalDateTime now = LocalDateTime.now();
        return List.of(
                stock("005930", "삼성전자", "KOSPI", "반도체", 400_0000_0000_0000L, now),
                stock("000660", "SK하이닉스", "KOSPI", "반도체", 100_0000_0000_0000L, now),
                stock("035720", "카카오", "KOSPI", "소프트웨어", 20_0000_0000_0000L, now),
                stock("035420", "NAVER", "KOSPI", "소프트웨어", 35_0000_0000_0000L, now),
                stock("005380", "현대자동차", "KOSPI", "자동차", 45_0000_0000_0000L, now),
                stock("000270", "기아", "KOSPI", "자동차", 35_0000_0000_0000L, now),
                stock("006400", "삼성SDI", "KOSPI", "2차전지", 25_0000_0000_0000L, now),
                stock("373220", "LG에너지솔루션", "KOSPI", "2차전지", 80_0000_0000_0000L, now),
                stock("051910", "LG화학", "KOSPI", "화학", 22_0000_0000_0000L, now),
                stock("055550", "신한지주", "KOSPI", "금융", 20_0000_0000_0000L, now),
                stock("105560", "KB금융", "KOSPI", "금융", 25_0000_0000_0000L, now),
                stock("068270", "셀트리온", "KOSPI", "바이오", 30_0000_0000_0000L, now),
                stock("207940", "삼성바이오로직스", "KOSPI", "바이오", 55_0000_0000_0000L, now),
                stock("247540", "에코프로비엠", "KOSDAQ", "2차전지", 15_0000_0000_0000L, now),
                stock("086520", "에코프로", "KOSDAQ", "2차전지", 10_0000_0000_0000L, now),
                stock("263750", "펄어비스", "KOSDAQ", "게임", 3_0000_0000_0000L, now),
                stock("293490", "카카오게임즈", "KOSDAQ", "게임", 2_0000_0000_0000L, now),
                stock("036570", "엔씨소프트", "KOSPI", "게임", 8_0000_0000_0000L, now),
                stock("028260", "삼성물산", "KOSPI", "건설", 18_0000_0000_0000L, now),
                stock("003550", "LG", "KOSPI", "지주", 12_0000_0000_0000L, now)
        );
    }

    private void createSamplePrices() {
        LocalDate today = LocalDate.now();
        // 최근 3일치 시세
        Object[][] data = {
                {"005930", 71000, 73000, 70500, 72500, 12_000_000L, "1.40"},
                {"000660", 185000, 190000, 184000, 188000, 5_000_000L, "2.17"},
                {"035720", 42000, 43500, 41500, 43000, 3_000_000L, "1.18"},
                {"035420", 195000, 200000, 193000, 198000, 1_500_000L, "1.54"},
                {"005380", 230000, 235000, 228000, 233000, 800_000L, "1.30"},
                {"000270", 115000, 118000, 114000, 117000, 1_200_000L, "1.74"},
                {"006400", 350000, 358000, 345000, 355000, 400_000L, "1.43"},
                {"373220", 380000, 390000, 375000, 385000, 300_000L, "1.32"},
                {"051910", 310000, 315000, 305000, 308000, 250_000L, "-0.65"},
                {"055550", 48000, 49000, 47500, 48500, 2_000_000L, "1.04"},
                {"105560", 72000, 73500, 71500, 73000, 1_800_000L, "1.39"},
                {"068270", 175000, 180000, 173000, 178000, 1_000_000L, "1.71"},
                {"207940", 780000, 800000, 775000, 795000, 100_000L, "1.92"},
                {"247540", 95000, 98000, 93000, 96000, 2_500_000L, "-1.03"},
                {"086520", 55000, 57000, 54000, 56500, 3_000_000L, "2.73"},
                {"263750", 35000, 36000, 34500, 35500, 500_000L, "1.43"},
                {"293490", 18000, 18500, 17500, 18200, 1_500_000L, "1.11"},
                {"036570", 195000, 198000, 192000, 193000, 300_000L, "-1.02"},
                {"028260", 125000, 128000, 124000, 127000, 200_000L, "1.60"},
                {"003550", 85000, 87000, 84000, 86000, 150_000L, "1.18"}
        };

        for (Object[] d : data) {
            priceRepository.save(StockDailyPrice.builder()
                    .stockCode((String) d[0])
                    .tradeDate(today)
                    .openPrice((Integer) d[1])
                    .highPrice((Integer) d[2])
                    .lowPrice((Integer) d[3])
                    .closePrice((Integer) d[4])
                    .volume((Long) d[5])
                    .changeRate(new BigDecimal((String) d[6]))
                    .build());
        }
    }

    private StockMaster stock(String code, String name, String market, String sector,
                              long marketCap, LocalDateTime updatedAt) {
        return StockMaster.builder()
                .stockCode(code).stockName(name).market(market)
                .sector(sector).marketCap(marketCap).updatedAt(updatedAt)
                .build();
    }
}
