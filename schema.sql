CREATE TABLE IF NOT EXISTS documents
(
    docUrl      STRING PRIMARY KEY NOT NULL,
    content     STRING             NOT NULL,
    wordCount   INT                NOT NULL DEFAULT -1,
    pageRank    DOUBLE             NOT NULL DEFAULT -1,
    indexTime   INT                NOT NULL DEFAULT 0,
    currentTime INT                NOT NULL,
    pageTitle   STRING             NOT NULL
);

CREATE TABLE IF NOT EXISTS words
(
    word STRING PRIMARY KEY NOT NULL
);

CREATE TABLE IF NOT EXISTS word_document
(
    word      STRING NOT NULL,
    docUrl    STRING NOT NULL,
    wordCount INT    NOT NULL DEFAULT -1,
    PRIMARY KEY (word, docUrl),
    FOREIGN KEY (docUrl) REFERENCES documents (docUrl)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (word) REFERENCES words (word)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS web_graph
(
    srcDocUrl STRING NOT NULL,
    dstDocUrl STRING NOT NULL,
    PRIMARY KEY (srcDocUrl, dstDocUrl),
    FOREIGN KEY (srcDocUrl) REFERENCES documents (docUrl)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (dstDocUrl) REFERENCES documents (docUrl)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

INSERT OR IGNORE INTO documents (docUrl, content, wordCount, pageRank, indexTime, currentTime, pageTitle)
VALUES
    ('https://www.google.com', 'Google is a search engine', 5, 0.5, 0, 0, 'Google'),
    ('https://www.yahoo.com', 'Yahoo is a free search engine', 5, 0.5, 0, 0, 'Yahoo'),
    ('https://www.bing.com', 'Bing is a search engine engine', 5, 0.5, 0, 0, 'Bing'),
    ('https://www.wikipedia.org', 'Wikipedia is a free online encyclopedia', 5, 0.5, 0, 0, 'Wikipedia'),
    ('https://www.cnn.com', 'CNN is a news organization', 5, 0.5, 0, 0, 'CNN');

INSERT OR IGNORE INTO words (word)
VALUES
    ('search'),
    ('engine'),
    ('news'),
    ('free'),
    ('online');

INSERT OR IGNORE INTO word_document (word, docUrl, wordCount)
VALUES
    ('search', 'https://www.google.com', 1),
    ('engine', 'https://www.google.com', 1),
    ('search', 'https://www.yahoo.com', 1),
    ('engine', 'https://www.yahoo.com', 1),
    ('free', 'https://www.yahoo.com', 1),
    ('search', 'https://www.bing.com', 1),
    ('engine', 'https://www.bing.com', 2),
    ('news', 'https://www.cnn.com', 1),
    ('free', 'https://www.wikipedia.org', 1),
    ('online', 'https://www.wikipedia.org', 1);