package com.olehpodolin.services;

import com.olehpodolin.model.OtherProducts;
import com.olehpodolin.model.RichInCarbsProduct;
import com.olehpodolin.model.RichInFatsProduct;
import com.olehpodolin.model.RichInProteinProduct;
import com.olehpodolin.repositories.CarbsRepository;
import com.olehpodolin.repositories.FatsRepository;
import com.olehpodolin.repositories.OtherProductsRepository;
import com.olehpodolin.repositories.ProteinRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sorter {

    private CarbsRepository carbsRepository;
    private FatsRepository fatsRepository;
    private OtherProductsRepository otherProductsRepository;
    private ProteinRepository proteinRepository;

    public Sorter(CarbsRepository carbsRepository, FatsRepository fatsRepository,
                  OtherProductsRepository otherProductsRepository, ProteinRepository proteinRepository) {
        this.carbsRepository = carbsRepository;
        this.fatsRepository = fatsRepository;
        this.otherProductsRepository = otherProductsRepository;
        this.proteinRepository = proteinRepository;
        sort();
    }

    public void sort() {
        Matcher nameMatcher = Pattern.compile("([A-Z][a-z]+\\s*)+").matcher("");
        Matcher nutrientMatcher = Pattern.compile("\\d+\\.?\\d+").matcher("");
        String productName = null;
        double carbsAmount = 0;
        double fatsAmount = 0;
        double proteinAmount = 0;
        int count = 0;

        for (String line : Reader.read("resources//Products.txt")) {
            nameMatcher.reset(line);
            nutrientMatcher.reset(line);
            while (nameMatcher.find()) {
                productName = nameMatcher.group();
            }

            while (nutrientMatcher.find()) {
                count++;
                switch (count) {
                    case 1:
                        carbsAmount = Double.parseDouble(nutrientMatcher.group());
                    case 2:
                        fatsAmount = Double.parseDouble(nutrientMatcher.group());
                    case 3:
                        proteinAmount = Double.parseDouble(nutrientMatcher.group());
                    default:
                        break;
                }

                if (count == 3) {
                    count = 0;
                    if (carbsAmount < 60 && fatsAmount < carbsAmount && proteinAmount < carbsAmount)
                        otherProductsRepository.add(new OtherProducts(productName, carbsAmount, fatsAmount, proteinAmount));
                    else if (carbsAmount > 60 && carbsAmount > fatsAmount && carbsAmount > proteinAmount)
                        carbsRepository.add(new RichInCarbsProduct(productName, carbsAmount, fatsAmount, proteinAmount));
                    else if (fatsAmount > 80 && fatsAmount > carbsAmount && fatsAmount > proteinAmount)
                        fatsRepository.add(new RichInFatsProduct(productName, carbsAmount, fatsAmount, proteinAmount));
                    else if (proteinAmount > 60 && proteinAmount > carbsAmount && proteinAmount > fatsAmount)
                        proteinRepository.add(new RichInProteinProduct(productName, carbsAmount, fatsAmount, proteinAmount));
                }
            }
        }
    }
}
