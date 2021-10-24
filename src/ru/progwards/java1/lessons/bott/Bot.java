package ru.progwards.java1.lessons.bott;

//import org.telegram.telegrambots.ApiContextInitializer;
import ru.progwards.java1.testlesson.ProgwardsTelegramBot;

import java.util.Scanner;

//public class Bot extends ProgwardsTelegramBot {
//    private final String menu = "У нас есть суши, роллы, пицца,акции, напитки и десерт";
//    @Override
//    public String processMessage(String text) {
//        checkTags(text);
//        if (foundCount() == 1) {
//            if (checkLastFound("привет"))
//                return "«Aloha» , мой голодный друг!\n Что желаешь? "+ menu;
//            if (checkLastFound("конец"))
//                return "Спасибо за заказ.Много жрать вредно";
//            if (checkLastFound("дурак"))
//                return "О своей скромности я могу говорить часами";
//            return "Отлично, добавляю в заказ " + getLastFound() + " Желаешь что-то еще?";
//
//        }
//        if (foundCount() > 1)
//            return "Под твой запрос подходит: \n" + extract() + "Выбери что-то одно, и я добавлю это в заказ.Пример Спайси креветка заказ делать Спайси ";
//        return "Я тупой бот, возможно у нас этого нет, попробуй выбрать из меню. "+ menu;    }
//    public static void main(String[] args) {
//        System.out.println("Hello bot");
//
//
//
//        //ApiContextInitializer.init();
//        Bot bot = new Bot();
//        bot.username = "Makssushi24bot";
//        bot.token = "1256053990:AAH8AmuAMnaIz3g4MsUv3FiSCrq68k1GXAs";
//        bot.addTags("конец", "конец, все, стоп, нет");
//        bot.addTags("дурак", "дурак, придурок, идиот, тупой");
//
//        bot.addTags("привет", "привет, здрасьте, здравствуй, добр, день, вечер, утро, hi, hello");
//        bot.addTags("Торт тирамису", "десерт, тирамис, маскарпоне, бисквит");
//        bot.addTags("Торт медовик", "десерт, мед, бисквит");
//        bot.addTags("Эклеры", "десерт, заварной, крем, тесто");
//
//        bot.addTags("Острый лосось", "суши,  остр, лосос");
//        bot.addTags("Спайси креветка", "ролл,  сливочн, сыр,спайс,креветк, огурец, масаго, соус");
//        bot.addTags("Крабище ", "ролл, краб крем, чука, яки, краб , масаго, медовый");
//        bot.addTags("Нежный угорь", "ролл,  нежн, сыр, огурец, угор, унаги шеф, соус");
//        bot.addTags("Запеченный угорь", "суши,запечен,угор, рис");
//        bot.addTags("Тигровые креветка", "суши, тигров,яки соус, рис, креветк");
//        bot.addTags("Сочный сет", "акци, ролл сочный краб, ролл сочный лосось, ролл медовый лосось");
//        bot.addTags("Магия  любви 2.0", "акци, ролл Калифорния с креветкой , ролл томаго лосось, ролл с огурцом");
//        bot.addTags("Вегетарианский сет", "акци, ролл овощной , ролл с авокадо, ролл с огурцом");
//
//        bot.addTags("Кола", "напит, пить, кола");
//        bot.addTags("Холодный чай", "напит, пить, чай, липтон, лимон");
//        bot.addTags("Сок", "напит, пить, сок, апельсиноый, яблочный, вишневый");
//        bot.addTags("Пицца гавайская", "гавайск, пицц, ананас, куриц");
//        bot.addTags("Пицца маргарита", "маргарит, пицц, моцарелла, сыр, кетчуп, помидор");
//        bot.addTags("Пицца пеперони", "пеперони, пицц, салями, колбас, сыр, кетчуп, помидор");
//        bot.start();
//    }
//
//
//
//}
