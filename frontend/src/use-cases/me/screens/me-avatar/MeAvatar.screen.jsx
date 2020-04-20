import React, { useState } from "react";
import {
    DigitButton,
    DigitDesign,
    DigitLayout,
    DigitSelectFile,
    DigitText,
    useDigitTranslations,
    useDigitToast
} from "@cthit/react-digit-components";
import translations from "./MeAvatar.screen.translations";
import { uploadUserAvatar } from "../../../../api/image/put.image.api";
import useGammaUser from "../../../../common/hooks/use-gamma-user/useGammaUser";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";

const MeAvatar = () => {
    const [text] = useDigitTranslations(translations);
    const [file, setFile] = useState(null);
    const [queueToast] = useDigitToast();
    const user = useGammaUser();

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
                            width="500px"
                            src={user.avatarUrl}
                            alt={"Avatar"}
                        />
                    )}
                    <DigitSelectFile
                        image
                        onSelectFile={setFile}
                        selectedFileName={file != null ? file.name : null}
                    />
                    <DigitButton
                        primary
                        raised
                        disabled={file == null}
                        text={text.UploadImage}
                        onClick={() => {
                            uploadUserAvatar(file)
                                .then(response => {
                                    window.location.reload();
                                    queueToast({
                                        text: text.AvatarUploaded,
                                        duration: 3000
                                    });
                                })
                                .catch(error => {
                                    const code = statusCode(error);
                                    let errorMessage = text.UploadFailed;
                                    if (code === 413) {
                                        errorMessage = text.TooLargeFile;
                                    }
                                    queueToast({
                                        text: errorMessage,
                                        duration: 5000
                                    });
                                });
                        }}
                    />
                </DigitDesign.CardBody>
            </DigitDesign.Card>
        </DigitLayout.Center>
    );
};

export default MeAvatar;
