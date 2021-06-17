import React, { useEffect, useState } from "react";

import translations from "./EnforceUserAgreement.comp.translations.json";
import {
    DigitText,
    DigitDesign,
    useDigitTranslations,
    useDigitCustomDialog,
    DigitButton,
    DigitMarkdown
} from "@cthit/react-digit-components";
import axios from "axios";
import useDeleteAccount from "./use-delete-account/useDeleteAccount";
import { acceptUserAgreement } from "../../api/me/put.me.api";
import { useHistory } from "react-router-dom";

const EnforceUserAgreement = () => {
    const history = useHistory();
    const [text, activeLanguage] = useDigitTranslations(translations);
    const [userAgreement, setUserAgreement] = useState();
    const [showDialog] = useDigitCustomDialog();

    useEffect(() => {
        axios.get("/useragreement-" + activeLanguage + ".md").then(response => {
            setUserAgreement(response.data);
        });
    }, [activeLanguage]);

    const startDeleteAccountProcess = useDeleteAccount();

    return (
        <DigitDesign.Card
            padding={"16px"}
            margin={{ top: "32px", left: "auto", right: "auto" }}
            size={{
                maxWidth: "500px",
                width: "auto"
            }}
        >
            <DigitText.Heading2 text={text.Hi} />
            <DigitText.Text text={text.AcceptNewUserAgreement} />
            <DigitButton
                text={text.ShowUserAgreement}
                outlined
                margin={{ top: "16px" }}
                alignSelf={"center"}
                size={{ width: "280px" }}
                onClick={() => {
                    showDialog({
                        renderMain: () => (
                            <DigitMarkdown
                                size={{
                                    minWidth: "320px",
                                    width: "100%",
                                    maxWidth: "600px"
                                }}
                                markdownSource={userAgreement}
                            />
                        ),
                        renderButtons: confirm => (
                            <DigitButton
                                primary
                                raised
                                text={text.Close}
                                onClick={confirm}
                            />
                        )
                    });
                }}
            />
            <DigitButton
                outlined
                text={text.DeleteYourAccount}
                alignSelf={"center"}
                size={{ width: "280px" }}
                onClick={startDeleteAccountProcess}
            />
            <DigitButton
                outlined
                text={text.IAccept}
                alignSelf={"center"}
                size={{ width: "280px" }}
                onClick={() => acceptUserAgreement().then(() => history.go(0))}
            />
        </DigitDesign.Card>
    );
};

export default EnforceUserAgreement;
