import formsPlugin from "@tailwindcss/forms";
import typographyPlugin from '@tailwindcss/typography';

/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [ "../resources/templates/**/*.{html,js}" ],
    theme: {
        extend: {
        },
    },
    plugins: [
        formsPlugin,
        typographyPlugin,
    ],
}

