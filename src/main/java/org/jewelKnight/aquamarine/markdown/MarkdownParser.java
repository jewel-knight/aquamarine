package com.impactcn.aquamarine.markdown;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ext.footnotes.Footnote;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.Strikethrough;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.*;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author impactCn
 * @date 2023/9/17 22:07
 */
public class MarkdownParser {


    private List<Range> ranges;

    /**
     * 配置选项
     */
    private final static MutableDataSet OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, Arrays.asList(
                    TablesExtension.create(),
                    StrikethroughExtension.create(),
                    FootnoteExtension.create()
            ));

    private final static Parser PARSER = Parser.builder(OPTIONS).build();

    private final static HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

    private volatile ArrayList<StyleRange> styleRanges;

    public String parse(String text) {

        // You can re-use parser and renderer instances
        Node document = PARSER.parse(text);
        styleRanges = new ArrayList<>();
//        List<Range> ranges = new ArrayList<>();
//        List<String> items = new ArrayList<>();
//        visitOrders(ranges, items, document);
//        this.ranges = index(items, ranges);

        visit(document);

        return RENDERER.render(document);
    }

    public List<StyleRange> getRanges() {
        return styleRanges;
    }


//    private void visitOrders(List<Range> ranges, List<String> items, Node document) {
//        NodeVisitor nodeVisitor = new NodeVisitor().addHandlers(new VisitHandler[]{new VisitHandler<>(OrderedList.class, new Visitor<OrderedList>() {
//            public void visit(OrderedList node) {
//                for (Node child : node.getChildren()) {
//                    OrderedListItem item = (OrderedListItem) child;
//                    ranges.add(item.getOpeningMarker().getSourceRange());
//                    items.add(item.getOpeningMarker().toString());
//                }
//            }
//        })});
//        nodeVisitor.visit(document);
//    }

    private void visit(Node node) {
        NodeVisitor visitor = new NodeVisitor(
                new VisitHandler<>(Heading.class, this::visit),
                new VisitHandler<>(Code.class, this::visit),
                new VisitHandler<>(HtmlBlock.class, this::visit),
                new VisitHandler<>(OrderedListItem.class, this::visit),
                new VisitHandler<>(BulletListItem.class, this::visit),
                new VisitHandler<>(BlockQuote.class, this::visit),
                new VisitHandler<>(FencedCodeBlock.class, this::visit),
                new VisitHandler<>(Emphasis.class, this::visit),
                new VisitHandler<>(StrongEmphasis.class, this::visit),
                new VisitHandler<>(TableCell.class, this::visit),
                new VisitHandler<>(TableSeparator.class, this::visit),
                new VisitHandler<>(Link.class, this::visit),
                new VisitHandler<>(ImageRef.class, this::visit),
                new VisitHandler<>(Image.class, this::visit),
                new VisitHandler<>(Reference.class, this::visit),
                new VisitHandler<>(LinkRef.class, this::visit),
                new VisitHandler<>(Footnote.class, this::visit),
                new VisitHandler<>(Strikethrough.class, this::visit)

        );

        visitor.visit(node);

    }

    /**
     * 标题
     * @param node
     */
    private void visit(Heading node) {
        setStyleClass(node.getOpeningMarker(), "heading");
        setStyleClass(node.getClosingMarker(), "heading");
        setStyleClass(node.getText(), "headingText");
    }

    /**
     * 代码
     * @param node
     */
    private void visit(Code node) {
        setStyleClass(node.getOpeningMarker(), "code");
        setStyleClass(node.getClosingMarker(), "code");
        setStyleClass(node.getText(), "codeText");
    }

    /**
     * html 块
     * @param node
     */
    private void visit(HtmlBlock node) {
        setStyleClass(node.getChars(), "htmlBlock");
    }

    /**
     * 有序列表
     * @param node
     */
    private void visit(OrderedListItem node) {
        setStyleClass(node.getOpeningMarker(), "orderedListItem");
    }

    /**
     * 无序列表
     * @param node
     */
    private void visit(BulletListItem node) {
        setStyleClass(node.getOpeningMarker(), "bulletListItem");
    }

    /**
     * 引用
     * @param node
     */
    private void visit(BlockQuote node) {
        setStyleClass(node.getOpeningMarker(), "blockQuote");
        setStyleClass(node.getChars(), "blockQuoteText");
    }

    /**
     * 代码块
     * @param node
     */
    private void visit(FencedCodeBlock node) {
        setStyleClass(node.getOpeningFence(), "fencedCodeBlock");
        setStyleClass(node.getClosingFence(), "fencedCodeBlock");
        setStyleClass(node.getContentChars(), "fencedCodeBlockText");
        setStyleClass(node.getInfo(), "fencedCodeBlockInfo");
    }

    /**
     * 强调
     * @param node
     */
    private void visit(Emphasis node) {
        setStyleClass(node.getOpeningMarker(), "emphasis");
        setStyleClass(node.getClosingMarker(), "emphasis");
    }

    /**
     * 强调重点
     * @param node
     */
    private void visit(StrongEmphasis node) {
        setStyleClass(node.getOpeningMarker(), "strongEmphasis");
        setStyleClass(node.getClosingMarker(), "strongEmphasis");
    }

    /**
     * 表格
     * @param tableCell
     */
    private void visit(TableCell tableCell) {
        setStyleClass(tableCell.getOpeningMarker(), "tableBlock");
        setStyleClass(tableCell.getClosingMarker(), "tableBlock");
    }

    /**
     * 表格分隔符
     * @param tableSeparator
     */
    private void visit(TableSeparator tableSeparator) {
        setStyleClass(tableSeparator.getChars(), "tableSeparator");
    }

    /**
     * 链接
     * @param link
     */
    private void visit(Link link) {
        setStyleClass(link.getTextOpeningMarker(), "linkText");
        setStyleClass(link.getTextClosingMarker(), "linkText");
        setStyleClass(link.getText(), "linkText");
        setStyleClass(link.getLinkOpeningMarker(), "linkHref");
        setStyleClass(link.getLinkClosingMarker(), "linkHref");
        setStyleClass(link.getUrl(), "linkUrl");
        setStyleClass(link.getTitleOpeningMarker(), "linkTitle");
        setStyleClass(link.getTitleClosingMarker(), "linkTitle");
        setStyleClass(link.getTitle(), "linkTitle");
    }

    /**
     * 图片引用
     * @param node
     */
    private void visit(ImageRef node) {
        setStyleClass(node.getTextOpeningMarker(), "imageRefText");
        setStyleClass(node.getTextClosingMarker(), "imageRefText");
        setStyleClass(node.getText(), "imageRefText");
        setStyleClass(node.getReferenceOpeningMarker(), "imageRefHref");
        setStyleClass(node.getReferenceClosingMarker(), "imageRefHref");
        setStyleClass(node.getReference(), "imageRefHref");
    }

    /**
     * 引用
     * @param node
     */
    private void visit(Reference node) {
        setStyleClass(node.getOpeningMarker(), "referenceText");
        BasedSequence closingMarker = node.getClosingMarker();
        setStyleClass(closingMarker.getStartOffset(), closingMarker.getEndOffset() - 1, "referenceText");
        setStyleClass(node.getReference(), "referenceText");
        setStyleClass(node.getUrl(), "referenceUrl");
        setStyleClass(node.getTitle(), "referenceTitle");
        setStyleClass(node.getTitleOpeningMarker(), "referenceTitle");
        setStyleClass(node.getTitleClosingMarker(), "referenceTitle");
    }

    /**
     * 图片
     * @param node
     */
    private void visit(Image node) {
        setStyleClass(node.getTextOpeningMarker(), "imageText");
        setStyleClass(node.getTextClosingMarker(), "imageText");
        setStyleClass(node.getText(), "imageText");
        setStyleClass(node.getLinkOpeningMarker(), "imageHref");
        setStyleClass(node.getLinkClosingMarker(), "imageHref");
        setStyleClass(node.getUrl(), "imageUrl");
        setStyleClass(node.getTitleOpeningMarker(), "imageTitle");
        setStyleClass(node.getTitleClosingMarker(), "imageTitle");
        setStyleClass(node.getTitle(), "imageTitle");
    }

    /**
     * 链接引用
     * @param node
     */
    private void visit(LinkRef node) {
        setStyleClass(node.getTextOpeningMarker(), "linkRefText");
        setStyleClass(node.getTextClosingMarker(), "linkRefText");
        setStyleClass(node.getText(), "linkRefText");
        setStyleClass(node.getReferenceOpeningMarker(), "linkRefHref");
        setStyleClass(node.getReferenceClosingMarker(), "linkRefHref");
        setStyleClass(node.getReference(), "linkRefHref");
    }

    /**
     * 脚注
     * @param node
     */
    private void visit(Footnote node) {
        setStyleClass(node.getChars(), "footnote");
    }

    /**
     * 删除线
     * @param node
     */
    private void visit(Strikethrough node) {
        setStyleClass(node.getChars(), "strikethrough");
    }



    /**
     * 设置样式
     * @param node
     * @param styleClass
     */
    private void setStyleClass(Node node, String styleClass) {
        setStyleClass(node.getChars(), styleClass);

    }

    /**
     * 设置样式
     * @param sequence
     * @param styleClass
     */
    private void setStyleClass(BasedSequence sequence, String styleClass) {
        int start = sequence.getStartOffset();
        int end = sequence.getEndOffset();
        addStyledRange(styleRanges, start, end, styleClass);
    }

    private void setStyleClass(int start, int end, String styleClass) {
        addStyledRange(styleRanges, start, end, styleClass);
    }

    private static void addStyledRange(ArrayList<StyleRange> styleRanges, int begin, int end, String styleClass) {
        final int lastIndex = styleRanges.size() - 1;

        // check whether list is empty
        if (styleRanges.isEmpty()) {
            styleRanges.add(new StyleRange(begin, end, styleClass));
            return;
        }

        // check whether new range is after last range
        final StyleRange lastRange = styleRanges.get(lastIndex);
        if (begin >= lastRange.end) {
            styleRanges.add(new StyleRange(begin, end, styleClass));
            return;
        }

        // walk existing ranges from last to first
        for (int i = lastIndex; i >= 0; i--) {
            StyleRange range = styleRanges.get(i);
            if (end <= range.begin) {
                // new range is before existing range (no overlapping) --> nothing yet to do
                continue;
            }

            if (begin >= range.end) {
                // existing range is before new range (no overlapping)

                if (begin < styleRanges.get(i+1).begin) {
                    // new range starts after this range (may overlap next range) --> add
                    int end2 = Math.min(end, styleRanges.get(i+1).begin);
                    styleRanges.add(i + 1, new StyleRange(begin, end2, styleClass));
                }

                break; // done
            }

            if (end > range.end) {
                // new range ends after this range (may overlap next range) --> add
                int end2 = (i == lastIndex) ? end : Math.min(end, styleRanges.get(i+1).begin);
                if (end2 > range.end)
                    styleRanges.add(i + 1, new StyleRange(range.end, end2, styleClass));
            }

            if (begin < range.end && end > range.begin) {
                // the new range overlaps the existing range somewhere

                if (begin <= range.begin && end >= range.end) {
                    // new range completely overlaps existing range --> merge style bits
                    styleRanges.set(i, new StyleRange(range.begin, range.end, range.styleClass));
                } else if (begin <= range.begin && end < range.end) {
                    // new range overlaps at the begin with existing range --> split range
                    styleRanges.set(i, new StyleRange(range.begin, end, range.styleClass));
                    styleRanges.add(i + 1, new StyleRange(end, range.end, range.styleClass));
                } else if (begin > range.begin && end >= range.end) {
                    // new range overlaps at the end with existing range --> split range
                    styleRanges.set(i, new StyleRange(range.begin, begin, range.styleClass));
                    styleRanges.add(i + 1, new StyleRange(begin, range.end, range.styleClass));
                } else if (begin > range.begin && end < range.end) {
                    // new range is in existing range --> split range
                    styleRanges.set(i, new StyleRange(range.begin, begin, range.styleClass));
                    styleRanges.add(i + 1, new StyleRange(begin, end, range.styleClass));
                    styleRanges.add(i + 2, new StyleRange(end, range.end, range.styleClass));
                }
            }
        }

        // check whether new range starts before first range
        if (begin < styleRanges.get(0).begin) {
            // add new range (part) before first range
            int end2 = Math.min(end, styleRanges.get(0).begin);
            styleRanges.add(0, new StyleRange(begin, end2, styleClass));
        }
    }



    private List<Range> index(List<String> items, List<Range> ranges) {
        for (int i = 0; i < items.size(); i++) {
            int item = sumStr(items.get(i));
            // 说明是有序列表全部不对
            // 95 代表 1.
            if (item != 95 + i) {
                return ranges.subList(i, ranges.size());
            }
        }

        return null;

    }

    private int sumStr(String str) {
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            sum += str.charAt(i);
        }
        return sum;
    }

    public static class StyleRange {
        private final String styleClass;
        private final int begin;
        private final int end;

        public StyleRange(int begin, int end, String styleClass) {
            this.styleClass = styleClass;
            this.begin = begin;
            this.end = end;
        }

        public String getStyleClass() {
            return styleClass;
        }

        public int getBegin() {
            return begin;
        }

        public int getEnd() {
            return end;
        }
    }


}
