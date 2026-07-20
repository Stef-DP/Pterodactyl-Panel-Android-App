package com.stefdp.pterodactylpanel.ui.theme

import com.neoutils.highlight.core.scheme.TextColorScheme
import com.neoutils.highlight.core.util.Matcher
import com.neoutils.highlight.core.util.UiColor

enum class HighlightLanguage {
    C,
    CPP,
    CSHARP,
    CSS,
    SASS,
    SCSS,
    HTML,
    JAVASCRIPT,
    TYPESCRIPT,
    VUE,
    PUG,
    SQL,
    MYSQL,
    MARIADB,
    POSTGRESQL,
    SQLITE,
    MS_SQL,
    CQL,
    DOCKERFILE,
    NGINX,
    HTTP,
    GOLANG,
    LUA,
    PYTHON,
    RUBY,
    RUST,
    PHP,
    SHELL,
    JSON,
    XML,
    YAML,
    TOML,
    PROPERTIES,
    DIFF,
    MARKDOWN,
    GIT_MARKDOWN,
    PLAIN_TEXT
}

val supportedLanguages = listOf(
    Pair(HighlightLanguage.C, "C"),
    Pair(HighlightLanguage.CPP, "C++"),
    Pair(HighlightLanguage.CSHARP, "C#"),
    Pair(HighlightLanguage.CSS, "CSS"),
    Pair(HighlightLanguage.SASS, "Sass"),
    Pair(HighlightLanguage.SCSS, "SCSS"),
    Pair(HighlightLanguage.HTML, "HTML"),
    Pair(HighlightLanguage.JAVASCRIPT, "JavaScript"),
    Pair(HighlightLanguage.TYPESCRIPT, "TypeScript"),
    Pair(HighlightLanguage.VUE, "Vue"),
    Pair(HighlightLanguage.PUG, "Pug"),
    Pair(HighlightLanguage.SQL, "SQL"),
    Pair(HighlightLanguage.MYSQL, "MySQL"),
    Pair(HighlightLanguage.MARIADB, "MariaDB"),
    Pair(HighlightLanguage.POSTGRESQL, "PostgreSQL"),
    Pair(HighlightLanguage.SQLITE, "SQLite"),
    Pair(HighlightLanguage.MS_SQL, "MS SQL"),
    Pair(HighlightLanguage.CQL, "CQL"),
    Pair(HighlightLanguage.DOCKERFILE, "Dockerfile"),
    Pair(HighlightLanguage.NGINX, "Nginx"),
    Pair(HighlightLanguage.HTTP, "HTTP"),
    Pair(HighlightLanguage.GOLANG, "Golang"),
    Pair(HighlightLanguage.LUA, "Lua"),
    Pair(HighlightLanguage.PYTHON, "Python"),
    Pair(HighlightLanguage.RUBY, "Ruby"),
    Pair(HighlightLanguage.RUST, "Rust"),
    Pair(HighlightLanguage.PHP, "PHP"),
    Pair(HighlightLanguage.SHELL, "Shell"),
    Pair(HighlightLanguage.JSON, "JSON"),
    Pair(HighlightLanguage.XML, "XML"),
    Pair(HighlightLanguage.YAML, "YAML"),
    Pair(HighlightLanguage.TOML, "TOML"),
    Pair(HighlightLanguage.PROPERTIES, "Properties"),
    Pair(HighlightLanguage.DIFF, "Diff"),
    Pair(HighlightLanguage.MARKDOWN, "Markdown"),
    Pair(HighlightLanguage.GIT_MARKDOWN, "Git Markdown"),
    Pair(HighlightLanguage.PLAIN_TEXT, "Plain Text")
)

val cHighlightColors = listOf(
    TextColorScheme(
        regex = "//.*|/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b(0x[0-9a-fA-F]+|\\d+(\\.\\d+)?)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(int|char|float|double|void|long|short|signed|unsigned|struct|union|enum|typedef|const|volatile|extern|static|register|auto)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b(if|else|while|do|for|return|switch|case|default|break|continue|goto)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b\\w+(?=\\s*\\()".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "(#\\s*(include|define|undef|ifdef|ifndef|if|else|elif|endif|error|pragma))\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF5555"))
    ),
)

val cppHighlightColors = listOf(
    TextColorScheme(
        regex = "//.*|/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b(0x[0-9a-fA-F]+|\\d+(\\.\\d+)?)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(int|char|float|double|void|bool|wchar_t|class|struct|union|enum|typedef|typename|template|namespace|using|public|protected|private|const|volatile|static|inline|virtual|explicit|friend|constexpr)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b(if|else|while|do|for|return|switch|case|default|break|continue|goto|try|catch|throw|new|delete|operator|this)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b\\w+(?=\\s*\\()".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "(#\\s*(include|define|undef|ifdef|ifndef|if|else|elif|endif|error|pragma))\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF5555"))
    ),
)

val csharpHighlightColors = listOf(
    TextColorScheme(
        regex = "//.*|/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b\\d+(\\.\\d+)?\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(class|struct|interface|enum|delegate|object|string|int|uint|long|ulong|short|ushort|byte|sbyte|float|double|decimal|bool|char|void|var|dynamic)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b(if|else|while|do|for|foreach|return|switch|case|default|break|continue|goto|yield|await|async|try|catch|finally|throw|new|this|base|abstract|as|is|checked|unchecked|fixed|lock|nameof|out|ref|readonly|sealed|static|unsafe|virtual|volatile)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b\\w+(?=\\s*\\()".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\[\\w+\\]".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\\b(using|namespace)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF5555"))
    ),
)

val cssHighlightColors = listOf(
    TextColorScheme(
        regex = "/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "(?:\\b|\\.|\\#)[a-zA-Z_-][a-zA-Z0-9_-]*(?=\\s*[\\{,])".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(color|background|margin|padding|width|height|border|display|position|top|left|right|bottom|font|flex|grid|box-shadow|opacity|cursor|z-index|transform|transition)\\b(?=\\s*:)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = ":\\s*([^;\\}]+)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "@(media|import|keyframes|font-face|charset|supports)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF5555"))
    ),
)

val sassHighlightColors = listOf(
    TextColorScheme(
        regex = "//.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "(\\$[a-zA-Z_-][a-zA-Z0-9_-]*)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(color|background|margin|padding|width|height|border|display|position|font|flex|opacity|cursor)\\b(?=\\s*:)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "@(mixin|include|extend|import|forward|use|function|return|if|else|for|each|while)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
)

val scssHighlightColors = listOf(
    TextColorScheme(
        regex = "//.*|/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "(\\$[a-zA-Z_-][a-zA-Z0-9_-]*)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(color|background|margin|padding|width|height|border|display|position|font|flex|opacity|cursor)\\b(?=\\s*:)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "@(mixin|include|extend|import|forward|use|function|return|if|else|for|each|while)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
)

val htmlHighlightColors = listOf(
    TextColorScheme(
        regex = "<!--[\\s\\S]*?-->".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "(<\\/?[a-zA-Z0-9:-]+)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(class|id|style|src|href|alt|type|value|name|placeholder|target|rel|onClick|disabled)\\b(?=\\s*=)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
)

val javascriptHighlightColors = listOf(
    TextColorScheme(
        regex = "//.*|/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'|`[^`]*`".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b\\d+(\\.\\d+)?\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(const|let|var|function|class|extends|constructor|import|export|from|default)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(if|else|while|do|for|in|of|return|switch|case|break|continue|try|catch|finally|throw|async|await|yield|new|this|typeof|instanceof|delete|void)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b\\w+(?=\\s*\\()".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b(true|false|null|undefined|NaN)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val typescriptHighlightColors = listOf(
    TextColorScheme(
        regex = "//.*|/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'|`[^`]*`".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b\\d+(\\.\\d+)?\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(const|let|var|function|class|interface|type|enum|extends|implements|constructor|import|export|from|namespace|as|declare|public|private|protected|readonly|abstract|static)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(if|else|while|do|for|in|of|return|switch|case|break|continue|try|catch|finally|throw|async|await|yield|new|this|typeof|instanceof|keyof|any|unknown|never|string|number|boolean|void)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b\\w+(?=\\s*\\()".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b(true|false|null|undefined)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val vueHighlightColors = listOf(
    TextColorScheme(
        regex = "<!--[\\s\\S]*?-->|//.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "(<\\/?[a-zA-Z0-9:-]+)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(v-[a-z:-]+|:[a-z-]+|@[a-z-]+|ref|key|slot)\\b(?=\\s*=)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b(export|default|data|methods|computed|watch|props|setup|mounted|created)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
)

val pugHighlightColors = listOf(
    TextColorScheme(
        regex = "//.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "^\\s*[a-zA-Z0-9_-]+".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "([.#][a-zA-Z0-9_-]+)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b(if|else|each|while|unless|case|when|default|block|extends|include|mixin)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
)

val sqlHighlightColors = listOf(
    TextColorScheme(
        regex = "(?m)--.*|/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b(SELECT|INSERT|UPDATE|DELETE|FROM|WHERE|JOIN|LEFT|RIGHT|INNER|OUTER|ON|GROUP\\s+BY|ORDER\\s+BY|HAVING|LIMIT|UNION|ALL|AS|VALUES|COMMIT|ROLLBACK)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(CREATE|TABLE|DATABASE|ALTER|DROP|INDEX|VIEW|PRIMARY\\s+KEY|FOREIGN\\s+KEY|NOT\\s+NULL|UNIQUE|DEFAULT|AUTO_INCREMENT|CHECK)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "(?i)\\b(true|false)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ), // Case-insensitive Booleans
    TextColorScheme(
        regex = "\\b\\d+(\\.\\d+)?\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(AND|OR|NOT|IN|EXISTS|BETWEEN|LIKE|IS\\s+NULL|NULL)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val mysqlHighlightColors = listOf(
    TextColorScheme(
        regex = "(?m)(#|--).*|/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B")
        ))
) + sqlHighlightColors + listOf(
    TextColorScheme(
        regex = "\\b(ENGINE|CHARSET|INT|VARCHAR|TEXT|DATETIME|TIMESTAMP)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD")
        ))
)

val mariadbHighlightColors = sqlHighlightColors + listOf(
    TextColorScheme(
        regex = "\\b(ENGINE|INT|VARCHAR|UUID|JSON)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    )
)

val postgresqlHighlightColors = sqlHighlightColors + listOf(
    TextColorScheme(
        regex = "\\b(SERIAL|BIGSERIAL|INT|VARCHAR|TEXT|UUID|JSONB|BOOLEAN|TIMESTAMP|WITH\\s+TIME\\s+ZONE|RETURNING)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    )
)

val sqliteHighlightColors = sqlHighlightColors + listOf(
    TextColorScheme(
        regex = "\\b(INTEGER|TEXT|REAL|BLOB|NONE|AUTOINCREMENT)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    )
)

val msSqlHighlightColors = sqlHighlightColors + listOf(
    TextColorScheme(
        regex = "@[a-zA-Z0-9_]+\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(TOP|NVARCHAR|DATETIME2|IDENTITY|DECLARE|SET|BEGIN|END|TRANSACTION)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    )
)

val cqlHighlightColors = listOf(
    TextColorScheme(
        regex = "(?m)--.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b(SELECT|INSERT|UPDATE|DELETE|FROM|WHERE|USING|AND|IN|LIMIT|ALLOW\\s+FILTERING|AS)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(CREATE|KEYSPACE|TABLE|ALTER|DROP|WITH|PRIMARY\\s+KEY|CLUSTERING\\s+ORDER|INT|TEXT|UUID|TIMESTAMP|MAP|LIST|SET)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    )
)

val dockerfileHighlightColors = listOf(
    TextColorScheme(
        regex = "#.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "^\\s*(FROM|RUN|CMD|LABEL|EXPOSE|ENV|ADD|COPY|ENTRYPOINT|VOLUME|USER|WORKDIR|ARG|ONBUILD|STOPSIGNAL|HEALTHCHECK|SHELL)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(AS)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
)

val nginxHighlightColors = listOf(
    TextColorScheme(
        regex = "#.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\\b(server|location|listen|server_name|root|index|proxy_pass|proxy_set_header|try_files|rewrite|return|access_log|error_log|ssl_certificate|ssl_certificate_key)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(on|off|worker_processes|worker_connections|keepalive_timeout)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val httpHighlightColors = listOf(
    TextColorScheme(
        regex = "^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|CONNECT|TRACE)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "HTTP\\/\\d\\.\\d".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "^([A-Z][a-zA-Z0-9-]+)(?=:)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = ":\\s*(.*)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
)

val golangHighlightColors = listOf(
    TextColorScheme(
        regex = "(?m)//.*|/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|`[^`]*`".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b(package|import|type|struct|interface|func|var|const|chan|map)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(if|else|switch|case|default|for|range|return|break|continue|fallthrough|go|select|defer|goto)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(int|int8|int16|int32|int64|uint|uint8|uint16|uint32|uint64|uintptr|float32|float64|complex64|complex128|string|bool|byte|rune|error)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b\\w+(?=\\s*\\()".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b\\d+(\\.\\d+)?\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(true|false|nil|iota)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val luaHighlightColors = listOf(
    TextColorScheme(
        regex = "--.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b(local|function|end|return)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(if|then|elseif|else|while|do|repeat|until|for|in|break)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b\\w+(?=\\s*\\()".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b(true|false|nil)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val pythonHighlightColors = listOf(
    TextColorScheme(
        regex = "(?m)#.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"\"\"[\\s\\S]*?\"\"\"|'''[\\s\\S]*?'''|\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b(def|class|global|nonlocal|import|from|as|lambda|pass|del)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(if|elif|else|while|for|break|continue|return|yield|try|except|finally|raise|assert|with|async|await)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(int|float|complex|str|bytes|bytearray|bool|list|tuple|set|dict|None|Any|Optional|Union|List|Dict|Tuple)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b\\w+(?=\\s*\\()".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b\\d+(\\.\\d+)?\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(True|False|and|or|not|in|is)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val rubyHighlightColors = listOf(
    TextColorScheme(
        regex = "#.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b(def|class|module|end|undef|alias|defined\\?)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(if|elsif|else|unless|while|until|for|break|next|redo|retry|return|yield|begin|rescue|ensure|raise|then|case|when)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b\\w+(?=\\s*\\()".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b(true|false|nil|and|or|not|self)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val rustHighlightColors = listOf(
    TextColorScheme(
        regex = "(?m)//.*|/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b(fn|struct|enum|union|trait|impl|type|mod|use|pub|const|static|let|mut|ref|as)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(if|else|while|loop|for|in|match|return|break|continue|async|await|unsafe|move|where)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(u8|u16|u32|u64|u128|usize|i8|i16|i32|i64|i128|isize|f32|f64|str|char|bool|String|Option|Result|Vec)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b\\w+!(?=\\s*\\(|\\s*\\[|\\s*\\{)|\\b\\w+!\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b\\w+(?=\\s*\\()".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\b\\d+(\\.\\d+)?\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b(true|false|Some|None|Ok|Err)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val phpHighlightColors = listOf(
    TextColorScheme(
        regex = "//.*|#.*|/\\*[\\s\\S]*?\\*/".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "<\\?php|\\?>".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF5555"))
    ),
    TextColorScheme(
        regex = "\\b(function|class|interface|trait|extends|implements|public|protected|private|static|const|namespace|use|global)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(if|elseif|else|while|do|for|foreach|switch|case|default|break|continue|return|try|catch|finally|throw|echo|print|die|exit|include|require)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\$\\w+".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
    TextColorScheme(
        regex = "\\b\\w+(?=\\s*\\()".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
)

val shellHighlightColors = listOf(
    TextColorScheme(
        regex = "#.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b(if|then|elif|else|fi|case|esac|for|while|until|do|done|in|return|exit|break|continue)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(echo|alias|export|local|unset|read|cd|pwd|ls|grep|awk|sed)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\$\\w+|\\$\\{\\w+\\}".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val jsonHighlightColors = listOf(
    TextColorScheme(
        regex = "\"[^\"]*\"(?=\\s*:)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = ":\\s*(\"[^\"]*\")".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "\\b(-?\\d+(\\.\\d+)?|true|false|null)\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val xmlHighlightColors = listOf(
    TextColorScheme(
        regex = "<!--[\\s\\S]*?-->".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "(<\\/?[a-zA-Z0-9:-]+)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b([a-zA-Z0-9:-]+)(?=\\s*=)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
)

val yamlHighlightColors = listOf(
    TextColorScheme(
        regex = "(?m)#.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "(?m)^\\s*([\\w.-]+)(?=\\s*:)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(true|false|null|yes|no|on|off)\\b|\\b\\d+\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val tomlHighlightColors = listOf(
    TextColorScheme(
        regex = "(?m)#.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "\"[^\"]*\"|'[^']*'".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "(?m)^\\[.*\\]".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "(?m)^\\s*([\\w.-]+)(?=\\s*=)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "\\b(true|false)\\b|\\d{4}-\\d{2}-\\d{2}|\\b\\d+\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val propertiesHighlightColors = listOf(
    TextColorScheme(
        regex = "(?m)^[#!].*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "(?m)^([^#!=:=\\s]+)(?=\\s*[=:])".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "[=:].*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
)

val diffHighlightColors = listOf(
    TextColorScheme(
        regex = "(?m)^\\+.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    ),
    TextColorScheme(
        regex = "(?m)^-.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF5555"))
    ),
    TextColorScheme(
        regex = "(?m)^@@.*@@".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "(?m)^diff.*|^index.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
)

val markdownHighlightColors = listOf(
    TextColorScheme(
        regex = "(?m)^#{1,6}\\s+.*".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "(?m)^\\s*([*+-]|\\d+\\.)\\s".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#FF79C6"))
    ),
    TextColorScheme(
        regex = "(\\*\\*|__)(.*?)\\1".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#F1FA8C"))
    ),
    TextColorScheme(
        regex = "`[^`]+`".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#8BE9FD"))
    ),
    TextColorScheme(
        regex = "\\[([^\\]]+)\\]\\(([^\\)]+)\\)".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#BD93F9"))
    ),
)

val gitMarkdownHighlightColors = markdownHighlightColors + listOf(
    TextColorScheme(
        regex = "\\b#[0-9]+\\b|@[a-zA-Z0-9_-]+\\b".toRegex(),
        matcher = Matcher.fully(UiColor.Hex("#50FA7B"))
    )
)

val languageToHighlightColors = mapOf(
    HighlightLanguage.C to cHighlightColors,
    HighlightLanguage.CPP to cppHighlightColors,
    HighlightLanguage.CSHARP to csharpHighlightColors,
    HighlightLanguage.CSS to cssHighlightColors,
    HighlightLanguage.SASS to sassHighlightColors,
    HighlightLanguage.SCSS to scssHighlightColors,
    HighlightLanguage.HTML to htmlHighlightColors,
    HighlightLanguage.JAVASCRIPT to javascriptHighlightColors,
    HighlightLanguage.TYPESCRIPT to typescriptHighlightColors,
    HighlightLanguage.VUE to vueHighlightColors,
    HighlightLanguage.PUG to pugHighlightColors,
    HighlightLanguage.SQL to sqlHighlightColors,
    HighlightLanguage.MYSQL to mysqlHighlightColors,
    HighlightLanguage.MARIADB to mariadbHighlightColors,
    HighlightLanguage.POSTGRESQL to postgresqlHighlightColors,
    HighlightLanguage.SQLITE to sqliteHighlightColors,
    HighlightLanguage.MS_SQL to msSqlHighlightColors,
    HighlightLanguage.CQL to cqlHighlightColors,
    HighlightLanguage.DOCKERFILE to dockerfileHighlightColors,
    HighlightLanguage.NGINX to nginxHighlightColors,
    HighlightLanguage.HTTP to httpHighlightColors,
    HighlightLanguage.GOLANG to golangHighlightColors,
    HighlightLanguage.LUA to luaHighlightColors,
    HighlightLanguage.PYTHON to pythonHighlightColors,
    HighlightLanguage.RUBY to rubyHighlightColors,
    HighlightLanguage.RUST to rustHighlightColors,
    HighlightLanguage.PHP to phpHighlightColors,
    HighlightLanguage.PLAIN_TEXT to emptyList(),
    HighlightLanguage.SHELL to shellHighlightColors,
    HighlightLanguage.JSON to jsonHighlightColors,
    HighlightLanguage.XML to xmlHighlightColors,
    HighlightLanguage.YAML to yamlHighlightColors,
    HighlightLanguage.TOML to tomlHighlightColors,
    HighlightLanguage.PROPERTIES to propertiesHighlightColors,
    HighlightLanguage.DIFF to diffHighlightColors,
    HighlightLanguage.MARKDOWN to markdownHighlightColors,
    HighlightLanguage.GIT_MARKDOWN to gitMarkdownHighlightColors
)