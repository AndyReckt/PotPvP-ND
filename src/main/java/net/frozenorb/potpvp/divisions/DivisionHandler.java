package net.frozenorb.potpvp.divisions;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.UUID;

public class DivisionHandler {

    public DivisionMeta divisionMeta;

    public DivisionHandler() {
        this.divisionMeta = new DivisionMeta();
        loadDivisions();
    }

    public void loadDivisions() {

        YamlConfiguration configuration = PotPvPND.getInstance().getDivisionsConfig().getConfiguration();
        String path = "Divivsions.";
        divisionMeta.setFirstDivision(CC.translate(configuration.getString(path + "1000 (First Division)")));
        divisionMeta.setSecondDivision(CC.translate(configuration.getString(path + "1030 (Second Division)")));
        divisionMeta.setThirdDivision(CC.translate(configuration.getString(path + "1050 (Third Division)")));
        divisionMeta.setFourthDivision(CC.translate(configuration.getString(path + "1075 (Fourth Division)")));
        divisionMeta.setFifthDivision(CC.translate(configuration.getString(path + "1100 (Fifth Division)")));
        divisionMeta.setSixthDivision(CC.translate(configuration.getString(path + "1150 (Sixth Divison)")));
        divisionMeta.setSeventhDivision(CC.translate(configuration.getString(path + "1300 (Seventh Division)")));
        divisionMeta.setEighthDivision(CC.translate(configuration.getString(path + "1350 (Eighth Division)")));
        divisionMeta.setNinthDivision(CC.translate(configuration.getString(path + "1400 (Ninth Division)")));
        divisionMeta.setTenthDivision(CC.translate(configuration.getString(path + "1450 (Tenth Division)")));
        divisionMeta.setEleventhDivision(CC.translate(configuration.getString(path + "1500 (Eleventh Division)")));
        divisionMeta.setTwelfthDivision(CC.translate(configuration.getString(path + "1600 (Twelfth Division)")));
        divisionMeta.setThirteenthDivision(CC.translate(configuration.getString(path + "1700 (Thirteenth Division)")));
        divisionMeta.setFourteenthDivision(CC.translate(configuration.getString(path + "1800 (Fourteenth Division)")));
        divisionMeta.setFifteenthDivision(CC.translate(configuration.getString(path + "1900 (Fifteenth Division)")));
        divisionMeta.setSixteenthDivision(CC.translate(configuration.getString(path + "2000 (Sixteenth Division)")));
        divisionMeta.setSeventeenthDivision(CC.translate(configuration.getString(path + "2100 (Seventeenth Division)")));
        divisionMeta.setEighteenthDivision(CC.translate(configuration.getString(path + "2200 (Eighteenth Division)")));
        divisionMeta.setNineteenthDivision(CC.translate(configuration.getString(path + "2300 (Ninteenth Division)")));
        divisionMeta.setTwentiethDivision(CC.translate(configuration.getString(path + "2400 (Twentieth Division)")));
        divisionMeta.setTwentyFirstDivison(CC.translate(configuration.getString(path + "2500 (TwentyFirst Division)")));
        divisionMeta.setTwentySecondDivision(CC.translate(configuration.getString(path + "2600 (TwentySecond Division)")));
        divisionMeta.setTwentyThirdDivision(CC.translate(configuration.getString(path + "2700 (TwentyThird Divison)")));
        divisionMeta.setTwentyFourthDivision(CC.translate(configuration.getString(path + "2800 (TwentyFourth Division)")));
        divisionMeta.setTwentyFifthDivision(CC.translate(configuration.getString(path + "2900 (TwentyFifth Division)")));
        divisionMeta.setTwentySixthDivision(CC.translate(configuration.getString(path + "3000 (TwentySixth Division)")));

        try {
            PotPvPND.getInstance().getDivisionsConfig().getConfiguration().save(PotPvPND.getInstance().getDivisionsConfig().getFile());
        } catch (IOException e) {
            //
        }
    }


    public String getDivision(UUID player) {
        EloHandler eloHandler = PotPvPND.getInstance().getEloHandler();
        int globalElo = eloHandler.getGlobalElo(player);
        String division;
        if (globalElo <= 1000) {
            division = divisionMeta.getFirstDivision();
        }
        if (globalElo >= 1030) {
            division = divisionMeta.getSecondDivision();
        }
        if (globalElo >= 1050) {
            division = divisionMeta.getThirdDivision();
        }
        if (globalElo >= 1075) {
            division = divisionMeta.getFourthDivision();
        }
        if (globalElo >= 1100) {
            division = divisionMeta.getFifthDivision();
        }
        if (globalElo >= 1150) {
            division = divisionMeta.getSixthDivision();
        }
        if (globalElo >= 1300) {
            division = divisionMeta.getSeventhDivision();
        }
        if (globalElo >= 1350) {
            division = divisionMeta.getEighthDivision();
        }
        if (globalElo >= 1400) {
            division = divisionMeta.getNinthDivision();
        }
        if (globalElo >= 1450) {
            division = divisionMeta.getTenthDivision();
        }
        if (globalElo >= 1500) {
            division = divisionMeta.getEleventhDivision();
        }
        if (globalElo >= 1600) {
            division = divisionMeta.getTwelfthDivision();
        }
        if (globalElo >= 1700) {
            division = divisionMeta.getThirteenthDivision();
        }
        if (globalElo >= 1800) {
            division = divisionMeta.getFourteenthDivision();
        }
        if (globalElo >= 1900) {
            division = divisionMeta.getFifteenthDivision();
        }
        if (globalElo >= 2000) {
            division = divisionMeta.getSixteenthDivision();
        }
        if (globalElo >= 2100) {
            division = divisionMeta.getSeventeenthDivision();
        }
        if (globalElo >= 2200) {
            division = divisionMeta.getEighteenthDivision();
        }
        if (globalElo >= 2300) {
            division = divisionMeta.getNineteenthDivision();
        }
        if (globalElo >= 2400) {
            division = divisionMeta.getTwentiethDivision();
        }
        if (globalElo >= 2500) {
            division = divisionMeta.getTwentyFirstDivison();
        }
        if (globalElo >= 2600) {
            division = divisionMeta.getTwentySecondDivision();
        }
        if (globalElo >= 2700) {
            division = divisionMeta.getTwentyThirdDivision();
        }
        if (globalElo >= 2800) {
            division = divisionMeta.getTwentyFourthDivision();
        }
        if (globalElo >= 2900) {
            division = divisionMeta.getTwentyFifthDivision();
        }
        if (globalElo >= 3000) {
            division = divisionMeta.getTwentySixthDivision();
        } else {
            return divisionMeta.getFirstDivision();
        }
        return division;
    }
}
