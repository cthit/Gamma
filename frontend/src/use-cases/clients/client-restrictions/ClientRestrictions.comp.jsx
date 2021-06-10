import React from "react";
import {
    DigitDesign,
    useDigitTranslations,
    DigitText
} from "@cthit/react-digit-components";
import { Link } from "react-router-dom";
import translations from "./ClientRestrictions.comp.translations.json";

const ClientRestrictions = ({ client }) => {
    const [text] = useDigitTranslations(translations);
    return (
        <DigitDesign.Card>
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={text.Restrictions} />
            </DigitDesign.CardHeader>
            <DigitDesign.CardBody>
                {client.restrictions.map(authorityLevel => (
                    <Link to={"/authorities/edit/" + authorityLevel}>
                        <DigitText.Text text={authorityLevel} />
                    </Link>
                ))}
            </DigitDesign.CardBody>
        </DigitDesign.Card>
    );
};

export default ClientRestrictions;
