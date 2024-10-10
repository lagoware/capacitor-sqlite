import { Sqlite } from '@lagoware&#x2F;capacitor-sqlite';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    Sqlite.echo({ value: inputValue })
}
