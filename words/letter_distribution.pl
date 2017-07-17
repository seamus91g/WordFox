

use 5.010;
use strict;
use warnings;
use Math::Round;


my %letterCounts;
	foreach my $letter ("A".."Z"){
		$letterCounts{$letter} = 0;
	}

# die;
# my $count = 0;
my $totalLetterCount = 0;
open my $infile, '<', "google-books-common-words_filtered.txt";
while (<$infile>){	
	my $line = $_;
	chomp($line);
	my @line = split("", $line);
	foreach my $letter (@line){
		$letterCounts{$letter} += 1;
		$totalLetterCount += 1;
	}

	# if ($count == 10){
	# 	last;
	# }
	# $count++;
}


close $infile;
open my $outfile, '>', "google-books-common-words_letter-distribution.txt";
print $outfile "Total number of letters: $totalLetterCount\n";
foreach my $key (sort {$letterCounts{$b} <=> $letterCounts{$a}} (keys %letterCounts) ){
	my $numberOfFinds = $letterCounts{$key};
	my $percent = nearest(.01, ($numberOfFinds/$totalLetterCount)*100);
	print $outfile "$key => $numberOfFinds\t" . $percent . "\n";
}
close $outfile;


