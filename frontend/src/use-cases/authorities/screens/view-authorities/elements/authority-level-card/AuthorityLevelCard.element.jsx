import React from "react";

import {
    DigitButton,
    DigitDesign,
    DigitList,
    DigitText,
    useDigitDialog,
    useDigitToast,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { deleteAuthorityLevel } from "api/authorities/delete.authoritites";

import translations from "./AuthorityLevelCard.element.translations";

const AuthorityLevelCard = ({
    authorityLevel,
    authorities = [],
    forceUpdate
}) => {
    const [text, activeLanguage] = useDigitTranslations(translations);
    const [openDialog] = useDigitDialog();
    const [queueToast] = useDigitToast();

    return (
        <DigitDesign.Card size={{ height: "400px" }}>
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={authorityLevel.authority} />
            </DigitDesign.CardHeader>
            <DigitDesign.CardBody>
                {authorities.length === 0 && (
                    <DigitText.Text alignCenter text={text.NoAuthorities} />
                )}
                {authorities.length > 0 && (
                    <div style={{ overflowY: "auto" }}>
                        <DigitList
                            items={authorities.map(authority => ({
                                text:
                                    authority.superGroup.prettyName +
                                    " - " +
                                    authority.post[activeLanguage]
                            }))}
                            onClick={null}
                            dense
                        />
                    </div>
                )}
            </DigitDesign.CardBody>
            <DigitDesign.CardButtons leftRight>
                <DigitButton
                    text={text.Delete}
                    onClick={() => {
                        openDialog({
                            title: text.AreYouSure,
                            description: text.AreYouSureDeleteAuthorityLevel,
                            confirmButtonText: text.ImSure,
                            cancelButtonText: text.Cancel,
                            onConfirm: () => {
                                deleteAuthorityLevel(authorityLevel.id)
                                    .then(() => {
                                        queueToast({
                                            text: text.DeleteSuccessful
                                        });
                                        forceUpdate();
                                    })
                                    .catch(() => {
                                        queueToast({
                                            text: text.DeleteFailed
                                        });
                                    });
                            }
                        });
                    }}
                />
                <DigitDesign.Link to={"/authorities/edit/" + authorityLevel.id}>
                    <DigitButton text={text.Edit} outlined />
                </DigitDesign.Link>
            </DigitDesign.CardButtons>
        </DigitDesign.Card>
    );
};

export default AuthorityLevelCard;
