import pandas as pd


def testcase_to_ints(token_to_int, text):
    '''
    句子转换成int形式，且output加上eos
    '''
    ints = []
    for sentence in text:
        sentence_ints = [token_to_int["<s>"]]
        for word in sentence.split():
            if word in token_to_int:
                sentence_ints.append(token_to_int[word])
            else:
                sentence_ints.append(token_to_int["<unk>"])
        sentence_ints.append(token_to_int["</s>"])
        ints.append(sentence_ints)
    return ints


def create_lengths(text):
    '''Create a data frame of the sentence lengths from a text'''
    lengths = []
    for sentence in text:
        lengths.append(len(sentence))
    return lengths


def loadDataset():
    reviews = pd.read_csv("/transformer/tc.csv", error_bad_lines=False)
    tests = pd.read_csv("./transformer/t.csv", error_bad_lines=False)

    # word2index
    special = ["<blank>", "<unk>", "<s>", "</s>"]
    src_word2idx = {}
    tgt_word2idx = {}
    index = 0  # 字典下标
    for token in special:
        src_word2idx[token] = index
        index += 1
    for sentence in reviews.output:
        for word in sentence.split():
            if word not in src_word2idx:
                src_word2idx[word] = index
                index += 1
    for sentence in tests.output:
        for word in sentence.split():
            if word not in src_word2idx:
                src_word2idx[word] = index
                index += 1
    print("数据集大小", len(src_word2idx))

    int_output = testcase_to_ints(src_word2idx, reviews.output)
    int_input = testcase_to_ints(src_word2idx, reviews.input)
    test_input = testcase_to_ints(src_word2idx, tests.input)
    test_output = testcase_to_ints(src_word2idx, tests.output)

    lengths_output = create_lengths(int_output)

    return src_word2idx, src_word2idx, int_input, int_output, test_input, test_output, max(lengths_output)


loadDataset()
