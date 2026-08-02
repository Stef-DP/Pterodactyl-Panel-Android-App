package com.stefdp.pterodactylpanel.ui.theme

import com.neoutils.highlight.core.scheme.TextColorScheme
import com.neoutils.highlight.core.util.Matcher
import com.neoutils.highlight.core.util.UiColor

enum class HighlightLanguage(
    val mimeTypes: List<String>,
    val extensions: List<String>
) {
    C(
        mimeTypes = listOf("text/x-csrc", "text/x-chdr"),
        extensions = listOf("c", "h")
    ),
    CPP(
        mimeTypes = listOf("text/x-c++src", "text/x-c++hdr"),
        extensions = listOf("cpp", "hpp", "cc", "hh", "cxx", "hxx")
    ),
    CSHARP(
        mimeTypes = listOf("text/x-csharp"),
        extensions = listOf("cs", "csx")
    ),
    CSS(
        mimeTypes = listOf("text/css"),
        extensions = listOf("css")
    ),
    SASS(
        mimeTypes = listOf("text/x-sass"),
        extensions = listOf("sass")
    ),
    SCSS(
        mimeTypes = listOf("text/x-scss"),
        extensions = listOf("scss")
    ),
    HTML(
        mimeTypes = listOf("text/html"),
        extensions = listOf("html", "htm")
    ),
    JAVASCRIPT(
        mimeTypes = listOf("application/javascript", "text/javascript", "application/ecmascript"),
        extensions = listOf("js", "mjs", "cjs")
    ),
    TYPESCRIPT(
        mimeTypes = listOf("application/typescript", "text/typescript"),
        extensions = listOf("ts", "mts", "cts")
    ),
    VUE(
        mimeTypes = listOf("text/x-vue", "application/x-vue"),
        extensions = listOf("vue")
    ),
    PUG(
        mimeTypes = listOf("text/x-pug", "text/x-jade"),
        extensions = listOf("pug", "jade")
    ),
    SQL(
        mimeTypes = listOf("application/sql", "text/x-sql"),
        extensions = listOf("sql")
    ),
    MYSQL(
        mimeTypes = listOf("text/x-mysql"),
        extensions = listOf("mysql")
    ),
    MARIADB(
        mimeTypes = listOf("text/x-mariadb"),
        extensions = listOf("mariadb")
    ),
    POSTGRESQL(
        mimeTypes = listOf("text/x-pgsql", "text/x-postgres"),
        extensions = listOf("pgsql", "postgres")
    ),
    SQLITE(
        mimeTypes = listOf("application/x-sqlite3", "application/sqlite3"),
        extensions = listOf("sqlite", "sqlite3", "db")
    ),
    MS_SQL(
        mimeTypes = listOf("text/x-mssql"),
        extensions = listOf("mssql", "tsql")
    ),
    CQL(
        mimeTypes = listOf("text/x-cql", "application/cql"),
        extensions = listOf("cql")
    ),
    DOCKERFILE(
        mimeTypes = listOf("text/x-dockerfile"),
        extensions = listOf("dockerfile", "Dockerfile")
    ),
    NGINX(
        mimeTypes = listOf("text/x-nginx-conf"),
        extensions = listOf("conf", "nginx")
    ),
    HTTP(
        mimeTypes = listOf("message/http", "application/x-httpd-php"),
        extensions = listOf("http", "rest")
    ),
    GOLANG(
        mimeTypes = listOf("text/x-go", "application/x-go"),
        extensions = listOf("go")
    ),
    LUA(
        mimeTypes = listOf("text/x-lua", "application/x-lua"),
        extensions = listOf("lua")
    ),
    PYTHON(
        mimeTypes = listOf("text/x-python", "application/x-python"),
        extensions = listOf("py", "pyw", "pyi")
    ),
    RUBY(
        mimeTypes = listOf("text/x-ruby", "application/x-ruby"),
        extensions = listOf("rb", "rbw")
    ),
    RUST(
        mimeTypes = listOf("text/x-rustsrc", "application/rust"),
        extensions = listOf("rs")
    ),
    PHP(
        mimeTypes = listOf("application/x-httpd-php", "text/x-php"),
        extensions = listOf("php", "phtml")
    ),
    SHELL(
        mimeTypes = listOf("text/x-sh", "application/x-sh", "text/x-shellscript"),
        extensions = listOf("sh", "bash", "zsh")
    ),
    JSON(
        mimeTypes = listOf("application/json", "text/x-json"),
        extensions = listOf("json")
    ),
    XML(
        mimeTypes = listOf("application/xml", "text/xml"),
        extensions = listOf("xml", "xsd", "xsl")
    ),
    YAML(
        mimeTypes = listOf("application/x-yaml", "text/yaml", "text/x-yaml"),
        extensions = listOf("yaml", "yml")
    ),
    TOML(
        mimeTypes = listOf("application/toml", "text/x-toml"),
        extensions = listOf("toml")
    ),
    PROPERTIES(
        mimeTypes = listOf("text/x-java-properties"),
        extensions = listOf("properties")
    ),
    DIFF(
        mimeTypes = listOf("text/x-diff", "text/x-patch"),
        extensions = listOf("diff", "patch")
    ),
    MARKDOWN(
        mimeTypes = listOf("text/markdown", "text/x-markdown"),
        extensions = listOf("md", "markdown")
    ),
    GIT_MARKDOWN(
        mimeTypes = listOf("text/x-git-markdown"),
        extensions = listOf("gitmd")
    ),
    ENV(
        mimeTypes = listOf("text/x-env"),
        extensions = listOf("env")
    ),
    PLAIN_TEXT(
        mimeTypes = listOf("text/plain"),
        extensions = listOf("txt")
    ),
    CRAWSSEMBLY(
        mimeTypes = emptyList(),
        extensions = listOf("craw")
    ),
    JAVA(
        mimeTypes = listOf("text/x-java-source", "text/x-java"),
        extensions = listOf("java")
    );

    companion object {
        private val mimeTypeMap: Map<String, HighlightLanguage> by lazy {
            entries.flatMap { language ->
                language.mimeTypes.map { mime -> mime.lowercase() to language }
            }.toMap()
        }

        private val extensionMap: Map<String, HighlightLanguage> by lazy {
            entries.flatMap { language ->
                language.extensions.map { ext -> ext.lowercase() to language }
            }.toMap()
        }

        fun fromMimeType(mimeType: String?): HighlightLanguage? {
            if (mimeType.isNullOrBlank()) return null

            return mimeTypeMap[mimeType.trim().lowercase()] ?: PLAIN_TEXT
        }

        fun fromExtension(extension: String?): HighlightLanguage? {
            if (extension.isNullOrBlank()) return null

            val cleanExt = extension.substringAfterLast('.').trim().lowercase()

            return extensionMap[cleanExt] ?: PLAIN_TEXT
        }
    }
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
    Pair(HighlightLanguage.ENV, "Environment"),
    Pair(HighlightLanguage.PLAIN_TEXT, "Plain Text"),
    Pair(HighlightLanguage.CRAWSSEMBLY, "Craw Assembly"),
    Pair(HighlightLanguage.JAVA, "Java")
)

val cHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(0x[0-9a-fA-F]+|\d+(\.\d+)?)\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\b[A-Z_][A-Z0-9_]*\b"""),
        matcher = Matcher.fully(colorConstantsAndEnums)
    ),
    TextColorScheme(
        regex = Regex("""\b(int|char|float|double|void|long|short|signed|unsigned|struct|union|enum|typedef|const|volatile|extern|static|register|auto)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b(if|else|while|do|for|return|switch|case|default|break|continue|goto)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""(#\s*(include|define|undef|ifdef|ifndef|if|else|elif|endif|error|pragma))\b"""),
        matcher = Matcher.fully(colorPreprocessorDirectives)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""//.*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val cppHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(0x[0-9a-fA-F]+|\d+(\.\d+)?)\b"""),
        matcher = Matcher.fully(colorPreprocessorNumbers)
    ),
    TextColorScheme(
        regex = Regex("""\b[A-Z_][A-Z0-9_]*\b"""),
        matcher = Matcher.fully(colorConstantsAndEnums)
    ),
    TextColorScheme(
        regex = Regex("""\b(int|char|float|double|void|bool|wchar_t|class|struct|union|enum|typedef|typename|template|namespace|using|public|protected|private|const|volatile|static|inline|virtual|explicit|friend|constexpr)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b(if|else|while|do|for|return|switch|case|default|break|continue|goto|try|catch|throw|operator|this)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\bnew\b"""),
        matcher = Matcher.fully(colorNewOperator)
    ),
    TextColorScheme(
        regex = Regex("""(#\s*(include|define|undef|ifdef|ifndef|if|else|elif|endif|error|pragma))\b"""),
        matcher = Matcher.fully(colorPreprocessorDirectives)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex("""<[^>\s]+>"""),
        matcher = Matcher.fully(colorPreprocessorStrings)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""//.*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val csharpHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b\d+(\.\d+)?\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\b[A-Z_][A-Z0-9_]*\b"""),
        matcher = Matcher.fully(colorConstantsAndEnums)
    ),
    TextColorScheme(
        regex = Regex("""\b(class|struct|interface|enum|delegate|object|string|int|uint|long|ulong|short|ushort|byte|sbyte|float|double|decimal|bool|char|void|var|dynamic)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b(if|else|while|do|for|foreach|return|switch|case|default|break|continue|goto|yield|await|async|try|catch|finally|throw|this|base|abstract|as|is|checked|unchecked|fixed|lock|nameof|out|ref|readonly|sealed|static|unsafe|virtual|volatile|using|namespace)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\bnew\b"""),
        matcher = Matcher.fully(colorNewOperator)
    ),
    TextColorScheme(
        regex = Regex("""\[\w+]"""),
        matcher = Matcher.fully(colorLabels)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""//.*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val cssHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""(?:\b|\.|#)[a-zA-Z_-][a-zA-Z0-9_-]*(?=\s*[{,])"""),
        matcher = Matcher.fully(colorCssClassesAndIDs)
    ),
    TextColorScheme(
        regex = Regex("""\b[a-zA-Z_-][a-zA-Z0-9_-]*(?=\s*\{)"""),
        matcher = Matcher.fully(colorCssTags)
    ),
    TextColorScheme(
        regex = Regex("""\b([a-zA-Z-]+)(?=\s*:)"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex(""":\s*([^;}]+)"""),
        matcher = Matcher.fully(colorCssPropertyValue)
    ),
    TextColorScheme(
        regex = Regex("""@(media|import|keyframes|font-face|charset|supports)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val sassHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""(\$[a-zA-Z_-][a-zA-Z0-9_-]*)\b"""),
        matcher = Matcher.fully(colorVariableAndParameterName)
    ),
    TextColorScheme(
        regex = Regex("""\b([a-zA-Z-]+)(?=\s*:)"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex("""@(mixin|include|extend|import|forward|use|function|return|if|else|for|each|while)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""//.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val scssHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""(\$[a-zA-Z_-][a-zA-Z0-9_-]*)\b"""),
        matcher = Matcher.fully(colorVariableAndParameterName)
    ),
    TextColorScheme(
        regex = Regex("""\b([a-zA-Z-]+)(?=\s*:)"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex("""@(mixin|include|extend|import|forward|use|function|return|if|else|for|each|while)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""//.*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val htmlHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""</?|>|/>"""),
        matcher = Matcher.fully(colorHtmlTagBrackets)
    ),
    TextColorScheme(
        regex = Regex("""(?<=</?)[a-zA-Z0-9:-]+"""),
        matcher = Matcher.fully(colorTags)
    ),
    TextColorScheme(
        regex = Regex("""\b([a-zA-Z0-9:-]+)(?=\s*=)"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex("""\\[nrt0"'\\]"""),
        matcher = Matcher.fully(colorEscapeCharacters)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""<!--[\s\S]*?-->"""),
        matcher = Matcher.fully(colorComment)
    )
)

val javascriptHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b[a-zA-Z_$\d]+\b"""),
        matcher = Matcher.fully(colorVariableAndParameterName)
    ),
    TextColorScheme(
        regex = Regex("""\b\d+(\.\d+)?\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\b(true|false|null|undefined|NaN|Infinity)\b"""),
        matcher = Matcher.fully(colorConstantsAndEnums)
    ),
    TextColorScheme(
        regex = Regex("""\b[A-Z][a-zA-Z0-9_$]*\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b(const|let|var|function|class|extends|constructor|import|export|from|default)\b"""),
        matcher = Matcher.fully(colorTags)
    ),
    TextColorScheme(
        regex = Regex("""\b(if|else|while|do|for|in|of|return|switch|case|break|continue|try|catch|finally|throw|async|await|yield|new|this|typeof|instanceof|delete|void)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b[a-zA-Z_$][a-zA-Z0-9_$]*(?=\s*:)"""),
        matcher = Matcher.fully(colorObjectKeysTSGrammar)
    ),
    TextColorScheme(
        regex = Regex("""\b[a-zA-Z_$][a-zA-Z0-9_$]*(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'|`([^`\\]|\\.)*`"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""//.*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    ),
)

val typescriptHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b\d+(\.\d+)?\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\b(true|false|null|undefined)\b"""),
        matcher = Matcher.fully(colorConstantsAndOptions)
    ),
    TextColorScheme(
        regex = Regex("""\b(any|unknown|never|string|number|boolean|void)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferencesTSGrammar)
    ),
    TextColorScheme(
        regex = Regex("""\b[A-Z_][A-Z0-9_]*\b"""),
        matcher = Matcher.fully(colorConstantsAndEnums)
    ),
    TextColorScheme(
        regex = Regex("""\b[a-zA-Z_$][a-zA-Z0-9_$]*(?=\s*:)"""),
        matcher = Matcher.fully(colorObjectKeysTSGrammar)
    ),
    TextColorScheme(
        regex = Regex("""\b(const|let|var|function|class|interface|type|enum|extends|implements|constructor|import|export|from|namespace|as|declare|public|private|protected|readonly|abstract|static|if|else|while|do|for|in|of|return|switch|case|break|continue|try|catch|finally|throw|async|await|yield|new|this|typeof|instanceof|keyof)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'|`([^`\\]|\\.)*`"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""//.*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val vueHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""</?[a-zA-Z0-9:-]+>?|>"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b([a-zA-Z0-9_:-]+|:[a-zA-Z0-9_-]+|@[a-zA-Z0-9_-]+)(?=\s*=)"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex("""\b(return|export|default|data|methods|computed|watch|props|setup|mounted|created|const|let|var|function|if|else|for|while|import|from)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b[a-zA-Z_$][a-zA-Z0-9_$]*(?=\s*:)"""),
        matcher = Matcher.fully(colorObjectKeysTSGrammar)
    ),
    TextColorScheme(
        regex = Regex("""\b\d+(\.\d+)?\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""<!--[\s\S]*?-->|//.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val pugHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""^\s*[a-zA-Z0-9_-]+"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""([.#][a-zA-Z0-9_-]+)"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex("""\b(if|else|each|while|unless|case|when|default|block|extends|include|mixin)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""//.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val sqlHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b\d+(\.\d+)?\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?i)\b(true|false)\b"""),
        matcher = Matcher.fully(colorConstantsAndOptions)
    ),
    TextColorScheme(
        regex = Regex("""\b(CREATE|TABLE|DATABASE|ALTER|DROP|INDEX|VIEW|PRIMARY\s+KEY|FOREIGN\s+KEY|NOT\s+NULL|UNIQUE|DEFAULT|AUTO_INCREMENT|CHECK)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b(SELECT|INSERT|UPDATE|DELETE|FROM|WHERE|JOIN|LEFT|RIGHT|INNER|OUTER|ON|GROUP\s+BY|ORDER\s+BY|HAVING|LIMIT|UNION|ALL|AS|VALUES|COMMIT|ROLLBACK|AND|OR|NOT|IN|EXISTS|BETWEEN|LIKE|IS\s+NULL|NULL)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?m)--.*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val mysqlHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(ENGINE|CHARSET|INT|VARCHAR|TEXT|DATETIME|TIMESTAMP)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    )
) + sqlHighlightColors.filterNot { it.matcher == Matcher.fully(colorComment) } + listOf(
    TextColorScheme(
        regex = Regex("""(?m)(#|--).*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val mariadbHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(ENGINE|INT|VARCHAR|UUID|JSON)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    )
) + sqlHighlightColors

val postgresqlHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(SERIAL|BIGSERIAL|INT|VARCHAR|TEXT|UUID|JSONB|BOOLEAN|TIMESTAMP|WITH\s+TIME\s+ZONE|RETURNING)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    )
) + sqlHighlightColors

val sqliteHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(INTEGER|TEXT|REAL|BLOB|NONE|AUTOINCREMENT)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    )
) + sqlHighlightColors

val msSqlHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""@[a-zA-Z0-9_]+\b"""),
        matcher = Matcher.fully(colorVariableAndParameterName)
    ),
    TextColorScheme(
        regex = Regex("""\b(TOP|NVARCHAR|DATETIME2|IDENTITY|DECLARE|SET|BEGIN|END|TRANSACTION)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    )
) + sqlHighlightColors

val cqlHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(CREATE|KEYSPACE|TABLE|ALTER|DROP|WITH|PRIMARY\s+KEY|CLUSTERING\s+ORDER|INT|TEXT|UUID|TIMESTAMP|MAP|LIST|SET)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b(SELECT|INSERT|UPDATE|DELETE|FROM|WHERE|USING|AND|IN|LIMIT|ALLOW\s+FILTERING|AS)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?m)--.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val dockerfileHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(FROM|RUN|CMD|LABEL|EXPOSE|ENV|ADD|COPY|ENTRYPOINT|VOLUME|USER|WORKDIR|ARG|ONBUILD|STOPSIGNAL|HEALTHCHECK|SHELL|AS)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""#.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val nginxHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(on|off|worker_processes|worker_connections|keepalive_timeout)\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\b(server|location|listen|server_name|root|index|proxy_pass|proxy_set_header|try_files|rewrite|return|access_log|error_log|ssl_certificate|ssl_certificate_key)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""#.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val httpHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""HTTP/\d\.\d"""),
        matcher = Matcher.fully(colorHeader)
    ),
    TextColorScheme(
        regex = Regex("""^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|CONNECT|TRACE)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""^([A-Z][a-zA-Z0-9-]+)(?=:)"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex(""":\s*(.*)"""),
        matcher = Matcher.fully(colorStringLiteral)
    )
)

val golangHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b\d+(\.\d+)?\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\b(true|false|nil|iota)\b"""),
        matcher = Matcher.fully(colorConstantsAndOptions)
    ),
    TextColorScheme(
        regex = Regex("""\b(int|int8|int16|int32|int64|uint|uint8|uint16|uint32|uint64|uintptr|float32|float64|complex64|complex128|string|bool|byte|rune|error)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b[A-Z_][A-Z0-9_]*\b"""),
        matcher = Matcher.fully(colorConstantsAndEnums)
    ),
    TextColorScheme(
        regex = Regex("""\b(package|import|type|struct|interface|func|var|const|chan|map|if|else|switch|case|default|for|range|return|break|continue|fallthrough|go|select|defer|goto)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|`([^`\\]|\\.)*`"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?m)//.*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val luaHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(true|false|nil)\b"""),
        matcher = Matcher.fully(colorConstantsAndOptions)
    ),
    TextColorScheme(
        regex = Regex("""\b(local|function|end|return|if|then|elseif|else|while|do|repeat|until|for|in|break)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""--.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val pythonHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b\d+(\.\d+)?\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\b(True|False|and|or|not|in|is)\b"""),
        matcher = Matcher.fully(colorConstantsAndOptions)
    ),
    TextColorScheme(
        regex = Regex("""\b(int|float|complex|str|bytes|bytearray|bool|list|tuple|set|dict|None|Any|Optional|Union|List|Dict|Tuple)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b[a-zA-Z_]\w*(?=\s*:|\s*=\s*|\s*])"""),
        matcher = Matcher.fully(colorPythonDictionaryKeys)
    ),
    TextColorScheme(
        regex = Regex("""\b(def|class|global|nonlocal|import|from|as|lambda|pass|del|if|elif|else|while|for|break|continue|return|yield|try|except|finally|raise|assert|with|async|await)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex("""${"\"\"\""}[\s\S]*?${"\"\"\""}|'''[\s\S]*?'''|"([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?m)#.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val rubyHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(true|false|nil|and|or|not|self)\b"""),
        matcher = Matcher.fully(colorConstantsAndOptions)
    ),
    TextColorScheme(
        regex = Regex("""@[a-zA-Z_]\w*"""),
        matcher = Matcher.fully(colorVariableAndParameterName)
    ),
    TextColorScheme(
        regex = Regex("""(:[a-zA-Z_]\w*|\battr_accessor|attr_reader|attr_writer)\b"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex("""\b(def|class|module|end|undef|alias|defined\?|if|elsif|else|unless|while|until|for|break|next|redo|retry|return|yield|begin|rescue|ensure|raise|then|case|when|puts|print)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""#.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val rustHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b\d+(\.\d+)?\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\b(true|false|Some|None|Ok|Err)\b"""),
        matcher = Matcher.fully(colorConstantsAndOptions)
    ),
    TextColorScheme(
        regex = Regex("""\b(u8|u16|u32|u64|u128|usize|i8|i16|i32|i64|i128|isize|f32|f64|str|char|bool|String|Option|Result|Vec)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b(fn|struct|enum|union|trait|impl|type|mod|use|pub|const|static|let|mut|ref|as|if|else|while|loop|for|in|match|return|break|continue|async|await|unsafe|move|where)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+!(?=\s*\(|\s*\[|\s*\{)|\b\w+!\b"""),
        matcher = Matcher.fully(colorCustomLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?m)//.*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val phpHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""<\?php|\?>"""),
        matcher = Matcher.fully(colorPreprocessorDirectives)
    ),
    TextColorScheme(
        regex = Regex("""\b\d+(\.\d+)?\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\$\w+"""),
        matcher = Matcher.fully(colorVariableAndParameterName)
    ),
    TextColorScheme(
        regex = Regex("""(?<=class\s)\w+|\b(string|int|float|bool|array|object|callable|iterable|void|never|mixed)\b|\b[A-Z]\w*\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b(function|class|interface|trait|extends|implements|public|protected|private|static|const|namespace|use|global|if|elseif|else|while|do|for|foreach|switch|case|default|break|continue|return|try|catch|finally|throw|echo|print|die|exit|include|require)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""//.*|#.*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val shellHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(true|false)\b"""),
        matcher = Matcher.fully(colorConstantsAndOptions)
    ),
    TextColorScheme(
        regex = Regex("""(?<=\b)[A-Za-z_]\w*(?=\s*=)|\$\w+|\$\{\w+\}"""),
        matcher = Matcher.fully(colorVariableAndParameterName)
    ),
    TextColorScheme(
        regex = Regex("""\b(if|then|elif|else|fi|case|esac|for|while|until|do|done|in|return|exit|break|continue)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b(echo|alias|export|local|unset|read|cd|pwd|ls|grep|awk|sed)\b"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""#.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val jsonHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""[{}\[\]()]"""),
        matcher = Matcher.fully(colorHtmlTagBrackets)
    ),
    TextColorScheme(
        regex = Regex("""\b(-?\d+(\.\d+)?|true|false|null)\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*""""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"\s*(?=:)"""),
        matcher = Matcher.fully(colorAttributes)
    )
)

val xmlHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""(</?[a-zA-Z0-9:-]+)"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b([a-zA-Z0-9:-]+)(?=\s*=)"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""<!--[\s\S]*?-->"""),
        matcher = Matcher.fully(colorComment)
    )
)

val yamlHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(true|false|null|yes|no|on|off)\b|\b\d+(\.\d+)?\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?m)^\s*([\w.-]+)(?=\s*:)"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex("""(?<=:\s)(?!true|false|null|yes|no|on|off|\d+)[a-zA-Z0-9_.-]+"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?m)#.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val tomlHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b(true|false)\b|\d{4}-\d{2}-\d{2}|\b\d+\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?m)^\s*([\w.-]+)(?=\s*=)"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex("""(?m)^\[.*]"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?m)#.*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val propertiesHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""(?m)^([^#!=:\s]+)(?=\s*[=:])"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex("""(?<=[=:]).*"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?m)^[#!].*"""),
        matcher = Matcher.fully(colorComment)
    )
)

val diffHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""(?m)^diff.*|^index.*"""),
        matcher = Matcher.fully(colorDiffHeader)
    ),
    TextColorScheme(
        regex = Regex("""(?m)^@@.*@@"""),
        matcher = Matcher.fully(colorDiffChanged)
    ),
    TextColorScheme(
        regex = Regex("""(?m)^-.*"""),
        matcher = Matcher.fully(colorDiffDeleted)
    ),
    TextColorScheme(
        regex = Regex("""(?m)^\+.*"""),
        matcher = Matcher.fully(colorDiffInserted)
    )
)

val markdownHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""(?m)^#{1,6}\s+.*"""),
        matcher = Matcher.fully(colorMarkdownHeading)
    ),
    TextColorScheme(
        regex = Regex("""(?m)^\s*([*+-]|\d+\.)\s"""),
        matcher = Matcher.fully(colorMarkdownListPunctuation)
    ),
    TextColorScheme(
        regex = Regex("""(?m)^\s*>.*"""),
        matcher = Matcher.fully(colorMarkdownQuotePunctuation)
    ),
    TextColorScheme(
        regex = Regex("""(\*\*|__)(.*?)\1"""),
        matcher = Matcher.fully(colorMarkdownBold)
    ),
    TextColorScheme(
        regex = Regex("""([*_])(.*?)\1"""),
        matcher = Matcher.fully(colorMarkdownItalic)
    ),
    TextColorScheme(
        regex = Regex("""\[([^]]+)]\(([^)]+)\)"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = Regex("""`[^`]+`"""),
        matcher = Matcher.fully(colorInlineCodeAndRawMarkup)
    )
)

val envHighlightColors = propertiesHighlightColors

val gitMarkdownHighlightColors = markdownHighlightColors.dropLast(1) + listOf(
    TextColorScheme(
        regex = Regex("""#\d+\b|@[a-zA-Z0-9_-]+\b"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    markdownHighlightColors.last()
)

val crawssemblyHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""(?<![\w])[-+]?0x[0-9A-Fa-f]+\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""(?<![\w])[-+]?\d+\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\br[0-9A-Fa-f]{2}\b|\b(r00|r01|ref|rff)\b"""),
        matcher = Matcher.fully(colorVariableAndParameterName)
    ),
    TextColorScheme(
        regex = Regex("""\b(sav|cal|io|inp|nop|screen|keyboard|speaker|mem|disk|text|time)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b(not|and|or|xor|shl|shr|sar|add|jmp|jmz|jmg|jml|ifz|ifg|ifl|rmv|fgo|str|run|stp)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b(char|hex|newline|pixel|read|write|erase|erasecell|x|y|int|clear|unix|low|sleep|milli|dump|present|red|green|blue|poll|btn|channel|freq|volume|wave|on|off|toggle|addr|save|iso|space)\b"""),
        matcher = Matcher.fully(colorAttributes)
    ),
    TextColorScheme(
        regex = """\b(execute|executestd)\b\s+.*$""".toRegex(RegexOption.MULTILINE),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = """;.*$""".toRegex(RegexOption.MULTILINE),
        matcher = Matcher.fully(colorComment)
    )
)

val javaHighlightColors = listOf(
    TextColorScheme(
        regex = Regex("""\b\d+(\.\d+)?\b"""),
        matcher = Matcher.fully(colorNumberLiteral)
    ),
    TextColorScheme(
        regex = Regex("""\b(true|false|null)\b"""),
        matcher = Matcher.fully(colorConstantsAndOptions)
    ),
    TextColorScheme(
        regex = Regex("""(?<=import\s|package\s)[a-zA-Z0-9_.]+(?=;)"""),
        matcher = Matcher.fully(colorJavaImportsAndPackageIdentifier)
    ),
    TextColorScheme(
        regex = Regex("""\b(class|interface|enum|record|extends|implements|void|int|long|double|float|boolean|char|byte|short)\b"""),
        matcher = Matcher.fully(colorTypesDeclarationAndReferences)
    ),
    TextColorScheme(
        regex = Regex("""\b(public|private|protected|static|final|abstract|synchronized|volatile|transient|native)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b(if|else|while|do|for|return|switch|case|default|break|continue|try|catch|finally|throw|throws|new|this|super|instanceof)\b"""),
        matcher = Matcher.fully(colorControlFlowAndSpecialKeywords)
    ),
    TextColorScheme(
        regex = Regex("""\b\w+(?=\s*\()"""),
        matcher = Matcher.fully(colorFunctionDeclarations)
    ),
    TextColorScheme(
        regex = Regex(""""([^"\\]|\\.)*"|'([^'\\]|\\.)*'"""),
        matcher = Matcher.fully(colorStringLiteral)
    ),
    TextColorScheme(
        regex = Regex("""//.*|/\*[\s\S]*?\*/"""),
        matcher = Matcher.fully(colorComment)
    )
)

val languageToHighlightColors = mapOf(
    HighlightLanguage.C to cHighlightColors,
    HighlightLanguage.CPP to cppHighlightColors,
    HighlightLanguage.CSHARP to csharpHighlightColors,
    HighlightLanguage.CSS to cssHighlightColors,
    HighlightLanguage.ENV to envHighlightColors,
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
    HighlightLanguage.GIT_MARKDOWN to gitMarkdownHighlightColors,
    HighlightLanguage.CRAWSSEMBLY to crawssemblyHighlightColors,
    HighlightLanguage.JAVA to javaHighlightColors
)