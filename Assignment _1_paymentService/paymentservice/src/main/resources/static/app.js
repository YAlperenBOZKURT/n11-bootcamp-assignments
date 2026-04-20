$(function () {
    const $method = $('#method');
    const $amount = $('#amount');
    const $pay = $('#pay');
    const $result = $('#result');

    function showResult(message, isSuccess) {
        $result
            .text(message)
            .removeClass('success error')
            .addClass(isSuccess ? 'success' : 'error')
            .show();
    }

    function loadMethods() {
        fetch('/api/payments/method-types')
            .then(res => res.json())
            .then(res => {
                const methods = res.data;
                const options = methods.map(m =>
                    `<option value="${m.id}">${m.displayName}</option>`
                );
                $method.html(options.join(''));
            });
    }

    function pay() {
        const amount = $amount.val();
        const paymentMethodTypeId = $method.val();

        if (!amount || Number(amount) <= 0) {
            showResult('Geçerli bir tutar girin', false);
            return;
        }

        const url = `/api/payments/pay?amount=${amount}&paymentMethodTypeId=${paymentMethodTypeId}`;

        fetch(url, { method: 'POST' })
            .then(res => res.json())
            .then(res => {
                if (!res.success) {
                    showResult(res.errorMessage || 'Ödeme başarısız', false);
                    return;
                }
                const data = res.data;
                const time = new Date(data.createdAt).toLocaleString('tr-TR');
                showResult(`${data.message} — ${data.amount} TL (${time})`, data.success);
            })
            .catch(() => showResult('Ödeme başarısız', false));
    }

    $pay.on('click', pay);
    loadMethods();
});
