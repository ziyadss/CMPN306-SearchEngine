package com.cmpn306.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: add separate filtering regex for urls
// TODO: add keywords/query specifics for phrase matching?
public class Filterer {
    private static final Pattern stopWords  = Pattern.compile(
            "\\b[^w -]*(a|a's|able|about|above|according|accordingly|across|actually|after|afterwards|again|against|ain't|all|allow|allows|almost|alone|along|already|also|although|always|am|among|amongst|an|and|another|any|anybody|anyhow|anyone|anything|anyway|anyways|anywhere|apart|appear|appreciate|appropriate|are|aren't|around|as|aside|ask|asking|associated|at|available|away|awfully|be|became|because|become|becomes|becoming|been|before|beforehand|behind|being|believe|below|beside|besides|best|better|between|beyond|both|brief|but|by|c'mon|c's|came|can|can't|cannot|cant|cause|causes|certain|certainly|changes|clearly|co|com|come|comes|concerning|consequently|consider|considering|contain|containing|contains|corresponding|could|couldn't|course|currently|de|definitely|described|despite|did|didn't|different|do|does|doesn't|doing|don't|done|down|downwards|during|each|edu|eg|eight|either|else|elsewhere|en|enough|entirely|especially|et|etc|even|ever|every|everybody|everyone|everything|everywhere|ex|exactly|example|except|far|few|fifth|first|five|followed|following|follows|for|former|formerly|forth|four|from|further|furthermore|get|gets|getting|given|gives|go|goes|going|gone|got|gotten|greetings|had|hadn't|happens|hardly|has|hasn't|have|haven't|having|he|he's|hello|help|hence|her|here|here's|hereafter|hereby|herein|hereupon|hers|herself|hi|him|himself|his|hither|hopefully|how|howbeit|however|i|i'd|i'll|i'm|i've|ie|if|ignored|immediate|in|inasmuch|inc|indeed|indicate|indicated|indicates|inner|insofar|instead|into|inward|is|isn't|it|it'd|it'll|it's|its|itself|just|keep|keeps|kept|know|known|knows|la|last|lately|later|latter|latterly|least|less|lest|let|let's|like|liked|likely|little|look|looking|looks|ltd|mainly|many|may|maybe|me|mean|meanwhile|merely|might|more|moreover|most|mostly|much|must|my|myself|name|namely|nd|near|nearly|necessary|need|needs|neither|never|nevertheless|new|next|nine|no|nobody|non|none|noone|nor|normally|not|nothing|novel|now|nowhere|obviously|of|off|often|oh|ok|okay|old|on|once|one|ones|only|onto|or|other|others|otherwise|ought|our|ours|ourselves|out|outside|over|overall|own|particular|particularly|per|perhaps|placed|please|plus|possible|presumably|probably|provides|que|quite|qv|rather|rd|re|really|reasonably|regarding|regardless|regards|relatively|respectively|right|said|same|saw|say|saying|says|second|secondly|see|seeing|seem|seemed|seeming|seems|seen|self|selves|sensible|sent|serious|seriously|seven|several|shall|she|should|shouldn't|since|six|so|some|somebody|somehow|someone|something|sometime|sometimes|somewhat|somewhere|soon|sorry|specified|specify|specifying|still|sub|such|sup|sure|t's|take|taken|tell|tends|th|than|thank|thanks|thanx|that|that's|thats|the|their|theirs|them|themselves|then|thence|there|there's|thereafter|thereby|therefore|therein|theres|thereupon|these|they|they'd|they'll|they're|they've|think|third|this|thorough|thoroughly|those|though|three|through|throughout|thru|thus|to|together|too|took|toward|towards|tried|tries|truly|try|trying|twice|two|un|und|under|unfortunately|unless|unlikely|until|unto|up|upo|us|use|used|useful|uses|using|usually|value|various|very|via|viz|vs|want|wants|was|wasn't|way|we|we'd|we'll|we're|we've|welcome|well|went|were|weren't|what|what's|whatever|when|whence|whenever|where|where's|whereafter|whereas|whereby|wherein|whereupon|wherever|whether|which|while|whither|who|who's|whoever|whole|whom|whose|why|will|willing|wish|with|within|without|won't|wonder|would|wouldn't|www|yes|yet|you|you'd|you'll|you're|you've|your|yours|yourself|yourselves|zero\n)[^\\w -]*\\b");

    private static final Pattern nonWords   = Pattern.compile("([^\\w\\s])+");
    private static final Pattern whiteSpace = Pattern.compile("\\s+");
    private static final Pattern htmlTags   = Pattern.compile("</?(!doctype html|a|abbr|address|area|article|aside" + "|audio|b|base|basefont|bdi|bdo|big|blockquote|body" + "|br" + "|button|canvas|caption|center|cite" + "|code" + "|col|colgroup|data|datalist|dd|del|details|dfn" + "|dialog|dir" + "|div" + "|dl|dt|em|embed|fieldset" + "|figcaption|figure|font|footer|form|frame|frameset|h1|head|header" + "|hr|html|i" + "|iframe|img" + "|input|ins|kbd|label|legend|li|link|main|map|mark|meta|meter|nav|noframes" + "|noscript" + "|object|ol|optgroup|option|output|p|param|picture|pre|progress|q|rp|rt|ruby|s|samp|" + "script|section" + "|select|small|source|span|strike|strong|sub|summary|sup|svg|table|tbody|td|template|" + "textarea|tfoot" + "|th|thead|time|title|tr|track|tt|u|ul|var|video|wbr) ?[^>]*>");
    private static final Pattern css        = Pattern.compile("<style ? [\\w\\W]*</style>");
    private static final Pattern bigWords   = Pattern.compile("([\\w]{15,})");
    private              String  text;

    public Filterer(String text) {
        this.text = text;
    }

    public static String rawText(String html) {
        return new Filterer(html).removeCss().removeHtmlTags().removeWhiteSpace().removeNonWords().text.trim();
    }

    public static List<String> getKeyWords(String html) {
        return new Filterer(html).toLower()
                                 .removeCss()
                                 .removeHtmlTags()
                                 .removeStopWords()
                                 .removeWhiteSpace()
                                 .removeBigWords()
                                 .split()
                                 .map(Stemmer::stem)
                                 .distinct()
                                 .filter((s) -> !s.equals("") && !s.equals(" "))
                                 .collect(Collectors.toList());
    }

    private Filterer toLower() {
        this.text = text.toLowerCase();
        return this;
    }

    private Filterer removeStopWords() {
        this.text = stopWords.matcher(text).replaceAll("");
        return this;

    }

    private Filterer removeHtmlTags() {
        text = htmlTags.matcher(text).replaceAll("");
        return this;
    }

    private Filterer removeCss() {
        text = css.matcher(text).replaceAll("");
        return this;
    }

    private Filterer removeNonWords() {
        text = nonWords.matcher(text).replaceAll(" ");
        return this;
    }

    private Filterer removeWhiteSpace() {
        text = whiteSpace.matcher(text).replaceAll("");
        return this;
    }

    private Filterer removeBigWords() {
        text = bigWords.matcher(text).replaceAll("");
        return this;
    }

    private Stream<String> split() {
        return Arrays.stream(text.split("\\s"));
    }
}
