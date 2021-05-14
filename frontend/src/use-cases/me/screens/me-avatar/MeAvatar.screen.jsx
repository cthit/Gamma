import React, { useContext, useState } from "react";
import { useHistory } from "react-router-dom";

import {
    DigitButton,
    DigitDesign,
    DigitLayout,
    DigitSelectFile,
    DigitText,
    useDigitTranslations,
    useDigitToast
} from "@cthit/react-digit-components";

import { uploadUserAvatar } from "api/image/put.image.api";

import GammaUserContext from "common/context/GammaUser.context";
import statusCode from "common/utils/formatters/statusCode.formatter";

import translations from "./MeAvatar.screen.translations";

const MeAvatar = () => {
    const history = useHistory();
    const [text] = useDigitTranslations(translations);
    const [file, setFile] = useState(null);
    const [queueToast] = useDigitToast();
    const [user, update] = useContext(GammaUserContext);

    return (
        <DigitLayout.Center>
            <DigitDesign.Card>
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle text={text.MeAvatarTitle} />
                </DigitDesign.CardHeader>
                <DigitDesign.CardBody>
                    {user.avatarUrl == null && (
                        <DigitText.Text bold text={text.NoAvatar} />
                    )}
                    {user.avatarUrl != null && (
                        <img
                            width="260px"
                            src={user.avatarUrl}
                            alt={"Avatar"}
                            style={{ alignSelf: "center" }}
                        />
                    )}
                    <DigitSelectFile
                        image
                        onSelectFile={setFile}
                        selectedFileName={file != null ? file.name : null}
                    />
                    <DigitLayout.Row justifyContent={"space-between"}>
                        <DigitButton
                            text={text.Back}
                            onClick={() => history.goBack()}
                            outlined
                        />
                        <DigitButton
                            primary
                            raised
                            disabled={file == null}
                            text={text.UploadImage}
                            onClick={() => {
                                uploadUserAvatar(file)
                                    .then(() => {
                                        update();
                                    })
                                    .catch(error => {
                                        const code = statusCode(error);
                                        let errorMessage = text.UploadFailed;
                                        if (code === 413) {
                                            errorMessage = text.TooLargeFile;
                                        } else if (code === 415) {
                                            errorMessage = text.InvalidFileType;
                                        }
                                        queueToast({
                                            text: errorMessage,
                                            duration: 5000
                                        });
                                    });
                            }}
                        />
                    </DigitLayout.Row>
                </DigitDesign.CardBody>
            </DigitDesign.Card>
        </DigitLayout.Center>
    );
};

export default MeAvatar;
