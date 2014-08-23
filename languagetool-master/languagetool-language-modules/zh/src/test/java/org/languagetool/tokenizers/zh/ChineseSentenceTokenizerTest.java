/* LanguageTool, a natural language style checker 
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package org.languagetool.tokenizers.zh;

import junit.framework.TestCase;

import org.languagetool.TestTools;
import org.languagetool.tokenizers.SentenceTokenizer;

/**
 * The test of ChineseSentenceTokenizer.
 *
 * @author Minshan Chen
 * @author Xiaohui Wu
 * @author Jiamin Zheng
 * @author Zihao Li
 */
public class ChineseSentenceTokenizerTest extends TestCase {

  private final SentenceTokenizer stokenizer = new ChineseSentenceTokenizer();

  public void testTokenize() {

    String t1 = "他说：";
    String t2 = "我们是中国人";
    String t3 = "中国人很好";

    char[] punctuation1 = { '_', '/', ';', ':', '!', '@', '#', '$', '%',
            '^', '&', '.', '+', '*', '?' };
    for (char i : punctuation1) {
      testSplit(t2 + i + t3);// 例子：我们是中国人_中国人很好
    }

    //char[] punctuation2 = { '，', '：', '…', '！', '？', '、', '；', '。' };
    char[] punctuation2 = { '\uff0c', '\uff1a', '\u2026', '\uff01', '\uff1f', '\u3001', '\uff1b', '\u3002' };
    for (char i : punctuation2) {
      testSplit(t2 + i, t3);// 例子：我们是中国人，/中国人很好
    }

    String[] punctuation3 = { "\"", "\'", "‘", "(", "（", "“", "”", "）",
            ")", "’", "\'", "\"" };
    for (int i = 0; i < punctuation3.length / 2; i++) {

      testSplit(t1, punctuation3[i], t2 + "，", t3
              + punctuation3[punctuation2.length - 1 - i]); // 例子:他说：/"/我们是中国人，/中国 人很好"
    }

    String[] punctuation4 = { "〝", "『", "«", "「", "〖", "{", "【", "[", "<",
            "《", "》", ">", "]", "】", "}", "〗", "」", "»", "』", "〞" };
    for (int i = 0; i < punctuation4.length / 2; i++) {
      testSplit(t1, punctuation4[i] + t2 + "，", t3
              + punctuation4[punctuation4.length - 1 - i]); // 他说：/〝我们是中国人，/中国人很好〞
    }

  }

  private void testSplit(final String... sentences) {
    TestTools.testSplit(sentences, stokenizer);
  }

}
