
const sideToogleBar = () => {

	if ($(".sidebar").is(":visible")) {

		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0");
		$(".barsBtn").css("display", "block");
		$(".heading").css("display", "none")
	}
	else {
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "17%");
		$(".heading").css("display", "block")
	}
}


function deleteContact(cid) {
	swal({
		title: "Are you sure?",
		text: "Are you want to delete this Contact!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((willDelete) => {
			if (willDelete) {
				window.location = "/user/delete/" + cid;
			} else {
				swal("Your Contact is safe");
			}
		});
}



const search = async () => {

	let query = $('#search-input').val();

	if (query == '') {

		$(".search-result").hide();

	}
	else {

		let url = `http://localhost:8002/search/${query}`;

		/*fetch(url).then((response) => {
		return response.json();
		}).then((result) =>{
			
			console.log(result);
		})*/

		let response = await fetch(url);

		let result = await response.json();

		let text = `<div class='list-group'>`;

		result.forEach((contact) => {
			text += `<a href='/user/contact/${contact.cid}' class='list-group-item list-group-item-action'> ${contact.name}</a>`
		})

		text += `</div>`
		$(".search-result").html(text);

		$(".search-result").show();

	}
}




const paymentStart = () => {

	let amount = $("#amounts").val();

	if (amount === '' || amount === null) {

		Swal.fire({
			icon: "error",
			text: "Amount is required",
		});
		return;
	}

	//amount ajax

	$.ajax(
		{
			url: "/user/create-order",
			data: JSON.stringify({ amount: amount, info: 'order-request' }),
			contentType: "application/json",
			type: "POST",
			dataType: 'json',

			success: function(response) {

				console.log(response);

				if (response.status === 'created') {

					let options = {
						key: 'rzp_test_RPMjbVWpGIUtWd',
						amount: response.amount,
						currency: "INR",
						name: "Smart Contact Manager",
						description: "Donation",
						order_id: response.id,
						"prefill":
						{
							"email": "",
							"contact": "",
						},
						config: {
							display: {
								blocks: {
									banks: {
										name: 'Most Used Methods',
										instruments: [
											{
												method: 'wallet',
												wallets: ['freecharge']
											},
											{
												method: 'upi'
											},
										],
									},
								},
								sequence: ['block.banks'],
								preferences: {
									show_default_blocks: true,
								},
							},
						},

						"handler": function(response) {

							updatePaymentOnServer(response.razorpay_payment_id, response.razorpay_order_id, 'paid');

							Swal.fire({
								position: "center",
								icon: "success",
								title: " Congrats!! your payment was successful",
								showConfirmButton: false,
								timer: 1500
							});
						},
						"modal": {
							"ondismiss": function() {
								if (confirm("Are you sure, you want to close the form?")) {
									txt = "You pressed OK!";
									console.log("Checkout form closed by the user");
								} else {
									txt = "You pressed Cancel!";
									console.log("Complete the Payment")
								}
							}
						}




					}

					var rzp1 = new Razorpay(options);
					rzp1.open();
				}


			},

			error: function(error) {
				console.log(error);
				Swal.fire({
					title:"Oops",
					icon: "error",
					text: "Something Went Wrong",
				});
			}


		}
	)



}


function updatePaymentOnServer(payment_id, order_id, status) {

	$.ajax({
		url: "/user/update-order",
		data: JSON.stringify({ payment_id: payment_id, order_id: order_id, status: status }),
		contentType: "application/json",
		type: "POST",
		dataType: 'json',

		success: function(response) {

			Swal.fire({
				position: "center",
				icon: "success",
				title: " Congrats!! your payment was successful",
				showConfirmButton: false,
				timer: 1500
			});
		},

		error: function(error) {

			Swal.fire({
				position: "center",
				icon: "error",
				title: " your payment was successful but we can't fetch the payment, we will contact you further",
				showConfirmButton: false,
				timer: 1500
			});
		}
	})







}