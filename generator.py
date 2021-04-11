''' Translate input text with trained model. '''import randomimport torchimport torch.utils.datafrom tqdm import tqdmfrom transformer.dataset import collate_fn, TranslationDatasetfrom transformer.Translator import Translatorfrom transformer.preprocess import convert_instance_to_idx_seqfrom transformer import word2indeximport transformer.Constants as Constantsimport numpy as npfrom rep import repimport click@click.command()@click.option("--model", default='./model/trained.chkpt', help="Path to load the model")@click.option("--vocab", default='./transformer/test_case.csv', help="Path to load the dataset")@click.argument("src")def read_instances_from_file(input, max_sent_len):    ''' Convert file into word seq lists and vocab '''    word_insts = []    trimmed_sent_count = 0    words = input.split()    if len(words) > max_sent_len:        trimmed_sent_count += 1    word_inst = words[:max_sent_len]    if word_inst:        word_insts += [[Constants.BOS_WORD] + word_inst + [Constants.EOS_WORD]]    else:        word_insts += [None]    print('[Info] Get input: {}'.format(input))    if trimmed_sent_count > 0:        print('[Warning] {} instances are trimmed to the max sentence length {}.'              .format(trimmed_sent_count, max_sent_len))    return word_instsclass setopt:    def __init__(self, model, vocab):        self.batch_size = 30        self.beam_size = 5        self.model = model        self.n_best = 5        self.no_cuda = 'True'        self.vocab = vocabdef generator(model, vocab, src):    global output    if src[0:8] != '[normal]':        src = '[normal]'    opt = setopt(model, vocab)    opt.cuda = not opt.no_cuda    opt.src = src    data = np.load('./transformer/data.npz')    src_word2idx = data['src_word2idx'].item()    tgt_word2idx = data['tgt_word2idx'].item()    max_token_seq_len = int(data['max_token_seq_len'])    test_src_word_insts = read_instances_from_file(        opt.src,        max_token_seq_len)    test_src_insts = convert_instance_to_idx_seq(        test_src_word_insts, src_word2idx)    test_loader = torch.utils.data.DataLoader(        TranslationDataset(            src_word2idx=src_word2idx,            tgt_word2idx=tgt_word2idx,            src_insts=test_src_insts),        num_workers=2,        batch_size=opt.batch_size,        collate_fn=collate_fn)    translator = Translator(opt)    output = []    for batch in tqdm(test_loader, leave=False):        all_hyp, all_scores = translator.translate_batch(*batch)        for idx_seqs in all_hyp:            for idx_seq in idx_seqs:                output.append(' '.join([test_loader.dataset.tgt_idx2word[idx] for idx in idx_seq]))    payload = output[random.randint(0, 4)]    output = rep(payload)    return output, payload