import js from '@eslint/js'
import globals from 'globals'
import react from 'eslint-plugin-react'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import tseslint from 'typescript-eslint'

export default tseslint.config(
    {ignores: ['dist']},
    {
        extends: [js.configs.recommended, ...tseslint.configs.strictTypeChecked],
        files: ['**/*.{ts,tsx}'],
        settings: {react: {version: '18.3'}},
        languageOptions: {
            ecmaVersion: 2020,
            globals: globals.browser,
            parserOptions: {
                project: ['./tsconfig.node.json', './tsconfig.app.json'],
                tsconfigRootDir: import.meta.dirname,
            },
        },
        plugins: {
            'react-hooks': reactHooks,
            'react-refresh': reactRefresh,
            'react': react,
        },
        rules: {
            ...reactHooks.configs.recommended.rules,
            "@typescript-eslint/no-unused-vars": "off",
            'react-refresh/only-export-components': [
                'warn',
                {allowConstantExport: true},
            ],
            ...react.configs.recommended.rules,
            ...react.configs['jsx-runtime'].rules,
            "react/prop-types": "off"

        },
    },
)
