CREATE TABLE IF NOT EXISTS documents
(
    pageTitle STRING             NOT NULL,
    docUrl    STRING PRIMARY KEY NOT NULL,
    content   STRING             NOT NULL,
    wordCount INT                NOT NULL DEFAULT -1,
    pageRank  DOUBLE             NOT NULL DEFAULT -1,
    indexTime INT                NOT NULL DEFAULT 0,
    crawlTime INT                NOT NULL DEFAULT 0,
    important BOOL               NOT NULL DEFAULT 0
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

CREATE TABLE IF NOT EXISTS query
(
    text STRING PRIMARY KEY NOT NULL
)